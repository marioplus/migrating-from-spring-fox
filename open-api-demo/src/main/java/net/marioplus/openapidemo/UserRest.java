package net.marioplus.openapidemo;

import io.swagger.v3.oas.annotations.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Tag(name = "用户相关接口")
@RestController
@RequestMapping("/user")
public class UserRest {

    @Operation(summary = "查找用户", description = "查找用户")
    @Parameter(name = "id", description = "编号", required = true)
    @GetMapping
    public User get(@RequestParam Long id) {
        return new User(id, "", 12);
    }

    @Operation(summary = "新增用户", description = "新增用户")
    @Parameters({ @Parameter(name = "id", description = "编号", required = true), @Parameter(name = "name", description = "姓名", required = true) })
    @Hidden()
    @PostMapping
    public Long add(@RequestParam Long id, @RequestParam String name) {
        return new User().getId();
    }

    @Operation(summary = "更新用户", description = "更新用户")
    @Parameters(@Parameter(name = "id", description = "编号", required = true))
    @ApiResponse(responseCode = "404", description = "foo")
    @PutMapping
    public Long update(@RequestParam Long id, @RequestBody User user) {
        return user.getId();
    }

    @Operation(summary = "删除用户", description = "删除用户")
    @Parameter(name = "id", description = "编号", required = true)
    @DeleteMapping
    public void delete(@RequestParam Long id) {
    }
}
