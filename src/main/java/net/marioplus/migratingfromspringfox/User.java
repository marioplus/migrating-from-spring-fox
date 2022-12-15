package net.marioplus.migratingfromspringfox;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "User", description = "用户")
public class User {

    @ApiModelProperty(name = "id", value = "编号", example = "1", required = true)
    private Long id;

    @ApiModelProperty(hidden = true, name = "name", value = "姓名", example = "小明", required = true)
    private String name;

    public User() {
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
