package net.marioplus.migratingfromspringfox;

import io.swagger.annotations.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "用户相关接口")
public class UserRest {

    @ApiOperation(value = "add", notes = "新增用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "name", value = "姓名", required = true, dataTypeClass = String.class),
    })
    public Long add(Long id, String name) {
        User user = new User(id, name);
        return user.getId();
    }

    @ApiOperation(value = "update", notes = "更新用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "name", value = "姓名", required = true, dataTypeClass = String.class),
    })
    @ApiResponse(code = 404, message = "foo")
    @ApiIgnore
    public void update(Long id, String name) {
        User user = new User(id, name);
    }


}
