package net.marioplus.openapidemo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "User", description = "用户")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Schema(name = "id", description = "编号", example = "1", required = true, nullable = true)
    private Long id;

    @Schema(name = "name", description = "姓名", example = "小明", required = true)
    private String name;

    @Schema(hidden = true, name = "age", description = "年龄", example = "12", required = true)
    private Integer age;
}
