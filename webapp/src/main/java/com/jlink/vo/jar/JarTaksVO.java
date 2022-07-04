package com.jlink.vo.jar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel("JAR任务创建")
public class JarTaksVO implements Serializable {
    @ApiModelProperty(value = "运行MainClass",dataType = "String",example = "com.jlink.JLinkApplication",required = true)
    private String mainClass;
    @ApiModelProperty(value = "作业描述信息",dataType = "String",example = "这个是作业描述信息",required = true)
    private String description;
}
