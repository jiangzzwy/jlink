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

package com.jlink.flink.yarn;

import org.apache.flink.util.FlinkException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * Yarn local resource descriptor is generated by {@link YarnClusterDescriptor} and set to
 * environment of job manager. Then it will be used to register local resources for task manager in
 * {@link Utils#createTaskExecutorContext}.
 */
class YarnLocalResourceDescriptor {

    private static final String STRING_FORMAT =
            "YarnLocalResourceDescriptor{"
                    + "key=%s, path=%s, size=%d, modificationTime=%d, visibility=%s, type=%s}";
    private static final Pattern LOCAL_RESOURCE_DESC_FORMAT =
            Pattern.compile(
                    "YarnLocalResourceDescriptor\\{"
                            + "key=(\\S+), path=(\\S+), size=([\\d]+), modificationTime=([\\d]+), visibility=(\\S+), type=(\\S+)}");

    private final String resourceKey;
    private final Path path;
    private final long size;
    private final long modificationTime;
    private final LocalResourceVisibility visibility;
    private final LocalResourceType resourceType;

    YarnLocalResourceDescriptor(
            String resourceKey,
            Path path,
            long resourceSize,
            long modificationTime,
            LocalResourceVisibility visibility,
            LocalResourceType resourceType) {
        this.resourceKey = checkNotNull(resourceKey);
        this.path = checkNotNull(path);
        this.size = resourceSize;
        this.modificationTime = modificationTime;
        this.visibility = checkNotNull(visibility);
        this.resourceType = checkNotNull(resourceType);
    }

    boolean alreadyRegisteredAsLocalResource() {
        return this.visibility.equals(LocalResourceVisibility.PUBLIC);
    }

    String getResourceKey() {
        return resourceKey;
    }

    Path getPath() {
        return path;
    }

    long getSize() {
        return size;
    }

    long getModificationTime() {
        return modificationTime;
    }

    LocalResourceVisibility getVisibility() {
        return visibility;
    }

    LocalResourceType getResourceType() {
        return resourceType;
    }

    static YarnLocalResourceDescriptor fromString(String desc) throws Exception {
        Matcher m = LOCAL_RESOURCE_DESC_FORMAT.matcher(desc);
        boolean mat = m.find();
        if (mat) {
            return new YarnLocalResourceDescriptor(
                    m.group(1),
                    new Path(m.group(2)),
                    Long.parseLong(m.group(3)),
                    Long.parseLong(m.group(4)),
                    LocalResourceVisibility.valueOf(m.group(5)),
                    LocalResourceType.valueOf(m.group(6)));
        } else {
            throw new FlinkException("Error to parse YarnLocalResourceDescriptor from " + desc);
        }
    }

    static YarnLocalResourceDescriptor fromFileStatus(
            final String key,
            final FileStatus fileStatus,
            final LocalResourceVisibility visibility,
            final LocalResourceType resourceType) {
        checkNotNull(key);
        checkNotNull(fileStatus);
        checkNotNull(visibility);
        return new YarnLocalResourceDescriptor(
                key,
                fileStatus.getPath(),
                fileStatus.getLen(),
                fileStatus.getModificationTime(),
                visibility,
                resourceType);
    }

    @Override
    public String toString() {
        return String.format(
                STRING_FORMAT,
                resourceKey,
                path.toString(),
                size,
                modificationTime,
                visibility,
                resourceType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(resourceKey);
        result = 31 * result + Objects.hashCode(path);
        result = 31 * result + Objects.hashCode(size);
        result = 31 * result + Objects.hashCode(modificationTime);
        result = 31 * result + Objects.hashCode(visibility.toString());
        result = 31 * result + Objects.hashCode(resourceType.toString());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == YarnLocalResourceDescriptor.class) {
            YarnLocalResourceDescriptor that = (YarnLocalResourceDescriptor) obj;
            return Objects.equals(this.resourceKey, that.resourceKey)
                    && Objects.equals(path, that.path)
                    && Objects.equals(size, that.size)
                    && Objects.equals(modificationTime, that.modificationTime)
                    && Objects.equals(visibility, that.visibility)
                    && Objects.equals(resourceType, that.resourceType);
        }
        return false;
    }

    /**
     * Transforms this local resource descriptor to a {@link LocalResource}.
     *
     * @return YARN resource
     */
    public LocalResource toLocalResource() {
        return Utils.registerLocalResource(path, size, modificationTime, visibility, resourceType);
    }
}
