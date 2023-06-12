package net.marioplus.springfoxdemo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "User", description = "用户")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @ApiModelProperty(name = "id", value = "编号", example = "1", required = true, allowEmptyValue = true)
    private Long id;

    @ApiModelProperty(name = "name", value = "姓名", example = "小明", required = true)
    private String name;

    @ApiModelProperty(hidden = true, name = "age", value = "年龄", example = "12", required = true)
    private Integer age;

}
