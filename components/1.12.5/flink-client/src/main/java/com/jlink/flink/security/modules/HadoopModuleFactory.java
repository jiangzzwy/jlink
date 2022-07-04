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

package com.jlink.flink.security.modules;

import com.jlink.flink.yarn.configuration.CustomOptions;
import com.jlink.flink.security.SecurityConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.AnnotatedSecurityInfo;
import org.apache.hadoop.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A {@link SecurityModuleFactory} for {@link HadoopModule}. This checks if Hadoop dependencies are
 * available before creating a {@link HadoopModule}.
 */
public class HadoopModuleFactory implements SecurityModuleFactory {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopModuleFactory.class);

    @Override
    public SecurityModule createModule(SecurityConfiguration securityConfig) {
        // First check if we have Hadoop in the ClassPath. If not, we simply don't do anything.
        try {
            Class.forName(
                    "org.apache.hadoop.conf.Configuration",
                    false,
                    HadoopModule.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            LOG.info(
                    "Cannot create Hadoop Security Module because Hadoop cannot be found in the Classpath.");
            return null;
        }

        try {
            if (!StringUtils.isBlank(securityConfig.getKeytab())) {
                System.setProperty("sun.security.krb5.debug", "false");
                System.setProperty("java.security.krb5.kdc", securityConfig.getKdc());
                System.setProperty("java.security.krb5.realm", securityConfig.getRealm());
            }
           SecurityUtil.setSecurityInfoProviders(new AnnotatedSecurityInfo());

            Configuration hadoopConfiguration = new Configuration();
            securityConfig.getFlinkConfig().get(CustomOptions.CUSTOM_HADOOP_FILES).stream()
                    .map(url-> {
                try {
                    return new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            }).filter(url->url!=null)
                .forEach(url-> {
                    try {
                        hadoopConfiguration.addResource(url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
               return new HadoopModule(securityConfig, hadoopConfiguration);
        } catch (LinkageError e) {
            LOG.error("Cannot create Hadoop Security Module.", e);
            return null;
        }
    }

}
