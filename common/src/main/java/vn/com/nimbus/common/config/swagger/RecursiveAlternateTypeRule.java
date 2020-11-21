package vn.com.nimbus.common.config.swagger;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import springfox.documentation.schema.AlternateTypeRule;

import java.util.List;
import java.util.stream.Stream;

public class RecursiveAlternateTypeRule extends AlternateTypeRule {
    private List<AlternateTypeRule> rules;
    public RecursiveAlternateTypeRule(TypeResolver typeResolver, List<AlternateTypeRule> rules) {
        // Unused but cannot be null
        super(typeResolver.resolve(Object.class), typeResolver.resolve(Object.class));
        this.rules = rules;
    }
    @Override
    public ResolvedType alternateFor(ResolvedType type) {
        Stream<ResolvedType> rStream = rules.stream().flatMap(rule -> Stream.of(rule.alternateFor(type)));
        ResolvedType newType = rStream
                .filter(alternateType -> alternateType != type).findFirst().orElse(type);
        if (appliesTo(newType)) {
            // Recursion happens here
            return alternateFor(newType);
        }
        return newType;
    }
    @Override
    public boolean appliesTo(ResolvedType type) {
        return rules.stream().anyMatch(rule -> rule.appliesTo(type));
    }
}