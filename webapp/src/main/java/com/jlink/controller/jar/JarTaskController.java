package com.jlink.controller.jar;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.jlink.vo.jar.JarTaksVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

@Api(tags = {"JAR包模块"})
@RestController
@RequestMapping("/jars")
public class JarTaskController {

    @ApiOperation(value = "创建JAR作业")
    @PostMapping(value = "/createJarTask")
    public ResponseEntity<String> createJarTask(
            @RequestPart("jar") MultipartFile jar,
            @RequestPart(name = "jobParam",required = false) JarTaksVO jarTaksVO){
        System.out.println(jar.getOriginalFilename());
        System.out.println(jarTaksVO);
        return ResponseEntity.ok("Hi:xxx");
    }
}
