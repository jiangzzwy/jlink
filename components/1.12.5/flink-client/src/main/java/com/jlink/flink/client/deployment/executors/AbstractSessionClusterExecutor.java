/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jlink.flink.client.deployment.executors;

import com.jlink.flink.client.ClientUtils;
import com.jlink.flink.client.deployment.ClusterClientFactory;
import com.jlink.flink.client.deployment.ClusterClientJobClientAdapter;
import com.jlink.flink.client.deployment.ClusterDescriptor;
import com.jlink.flink.client.program.ClusterClient;
import com.jlink.flink.client.program.ClusterClientProvider;
import com.jlink.flink.utils.DeployResult;
import com.jlink.flink.utils.DeployResultHolder;
import com.jlink.flink.yarn.YarnClusterDescriptor;
import com.jlink.flink.yarn.configuration.CustomOptions;
import org.apache.flink.annotation.Internal;
import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.core.execution.PipelineExecutor;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.jsonplan.JsonPlanGenerator;
import org.apache.flink.util.function.FunctionUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;

import javax.annotation.Nonnull;

import java.util.concurrent.CompletableFuture;

import static org.apache.flink.util.Preconditions.checkNotNull;
import static org.apache.flink.util.Preconditions.checkState;

/**
 * An abstract {@link PipelineExecutor} used to execute {@link Pipeline pipelines} on an existing
 * (session) cluster.
 *
 * @param <ClusterID> the type of the id of the cluster.
 * @param <ClientFactory> the type of the {@link ClusterClientFactory} used to create/retrieve a
 *     client to the target cluster.
 */
@Internal
public class AbstractSessionClusterExecutor<
                ClusterID, ClientFactory extends ClusterClientFactory<ClusterID>>
        implements PipelineExecutor {

    private final ClientFactory clusterClientFactory;

    public AbstractSessionClusterExecutor(@Nonnull final ClientFactory clusterClientFactory) {
        this.clusterClientFactory = checkNotNull(clusterClientFactory);
    }

    @Override
    public CompletableFuture<JobClient> execute(
            @Nonnull final Pipeline pipeline,
            @Nonnull final Configuration configuration,
            @Nonnull final ClassLoader userCodeClassloader)
            throws Exception {
        final JobGraph jobGraph = PipelineExecutorUtils.getJobGraph(pipeline, configuration);

        try (final ClusterDescriptor<ClusterID> clusterDescriptor =
                clusterClientFactory.createClusterDescriptor(configuration)) {
            final ClusterID clusterID = clusterClientFactory.getClusterId(configuration);
            checkState(clusterID != null);

            final ClusterClientProvider<ClusterID> clusterClientProvider =
                    clusterDescriptor.retrieve(clusterID);
            ClusterClient<ClusterID> clusterClient = clusterClientProvider.getClusterClient();

            //设置提交参数
            if(clusterDescriptor instanceof YarnClusterDescriptor){
                YarnClusterDescriptor yarnClusterDescriptor= (YarnClusterDescriptor) clusterDescriptor;
                final YarnClient yarnClient = yarnClusterDescriptor.getYarnClient();
                final ApplicationReport report = yarnClient.getApplicationReport((ApplicationId) clusterID);
                final DeployResult deployResult = DeployResultHolder.get();

                //收集yarn状态
                deployResult.setApplicationId(report.getApplicationId().toString());
                deployResult.setApplicationName(report.getName());
                deployResult.setApplicationType(report.getApplicationType());
                deployResult.setTrackingUrl(report.getOriginalTrackingUrl());
                deployResult.setYarnTrackingUrl(report.getTrackingUrl());
                deployResult.setState(report.getYarnApplicationState().name());
                deployResult.setFinalState(report.getFinalApplicationStatus().name());
                deployResult.setQueue(report.getQueue());
                deployResult.setUser(report.getUser());
                deployResult.setStartTime(report.getStartTime());
                deployResult.setApplicationAttemptId(report.getCurrentApplicationAttemptId().toString());
                deployResult.setDeployTarget(configuration.getString(DeploymentOptions.TARGET));
                deployResult.setBusinessId(configuration.getString(CustomOptions.CUSTOM_BUSINESS_ID));

                //收集job信息
                deployResult.setJobGraph(JsonPlanGenerator.generatePlan(jobGraph));
                deployResult.setJobId(jobGraph.getJobID().toString());
                deployResult.setJobName(jobGraph.getName());
                deployResult.setMaximumParallelism(jobGraph.getMaximumParallelism());
            }
            return clusterClient
                    .submitJob(jobGraph)
                    .thenApplyAsync(
                            FunctionUtils.uncheckedFunction(
                                    jobId -> {
                                        ClientUtils.waitUntilJobInitializationFinished(
                                                () -> clusterClient.getJobStatus(jobId).get(),
                                                () -> clusterClient.requestJobResult(jobId).get(),
                                                userCodeClassloader);
                                        return jobId;
                                    }))
                    .thenApplyAsync(
                            jobID ->
                                    (JobClient)
                                            new ClusterClientJobClientAdapter<>(
                                                    clusterClientProvider,
                                                    jobID,
                                                    userCodeClassloader))
                    .whenCompleteAsync((ignored1, ignored2) -> clusterClient.close());
        }
    }
}
