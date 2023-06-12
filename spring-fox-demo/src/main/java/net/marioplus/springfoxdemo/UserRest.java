package net.marioplus.springfoxdemo;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/user")
public class UserRest {

    @ApiOperation(value = "查找用户", notes = "查找用户")
    @ApiImplicitParam(name = "id", value = "编号", required = true, dataTypeClass = Long.class)
    @GetMapping
    public User get(@RequestParam Long id) {
        return new User(id, "", 12);
    }

    @ApiOperation(value = "新增用户", notes = "新增用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "name", value = "姓名", required = true, dataTypeClass = String.class),
    })
    @ApiIgnore("")
    @PostMapping
    public Long add(@RequestParam Long id, @RequestParam String name) {
        return new User().getId();
    }

    @ApiOperation(value = "更新用户", notes = "更新用户")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "编号", required = true, dataTypeClass = Long.class)
    )
    @ApiResponse(code = 404, message = "foo")
    @PutMapping
    public Long update(@RequestParam Long id, @RequestBody User user) {
        return user.getId();
    }

    @ApiOperation(value = "delete", notes = "删除用户")
    @ApiImplicitParam(name = "id", value = "编号", required = true, dataTypeClass = Long.class)
    @DeleteMapping
    public void delete(@RequestParam Long id) {
    }

}
