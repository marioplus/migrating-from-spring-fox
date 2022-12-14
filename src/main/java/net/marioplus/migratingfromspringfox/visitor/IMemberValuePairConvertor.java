package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.expr.MemberValuePair;
import io.swagger.v3.oas.annotations.media.Schema;

public interface IMemberValuePairConvertor {

    @Schema(accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    boolean filter(MemberValuePair name);

    void convert(MemberValuePair pair);
}
