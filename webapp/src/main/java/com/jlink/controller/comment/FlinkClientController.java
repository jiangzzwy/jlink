package com.jlink.controller.comment;


import com.jlink.vo.jar.JarTaksVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = {"JlinkClient模块"})
@RestController
@RequestMapping("/clients")
public class FlinkClientController {
    @ApiOperation(value = "创建JAR作业")
    @PostMapping(value = "/uploadJarTask")
    public ResponseEntity<String> uploadJarTask(@RequestPart("jar") MultipartFile jarTask){
        return  ResponseEntity.ok("SUCCESS");
    }
}
