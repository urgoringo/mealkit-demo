package com.github.kagkarlsson.shaded.cronutils.validation;

import com.github.kagkarlsson.shaded.cronutils.model.CronType;
import com.github.kagkarlsson.shaded.cronutils.model.definition.CronDefinition;
import com.github.kagkarlsson.shaded.cronutils.model.definition.CronDefinitionBuilder;
import com.github.kagkarlsson.shaded.cronutils.parser.CronParser;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CronValidator implements ConstraintValidator<Cron, String> {

    private CronType type;

    @Override
    public void initialize(Cron constraintAnnotation) {
        this.type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(type);
        CronParser cronParser = new CronParser(cronDefinition);
        try {
            cronParser.parse(value).validate();
            return true;
        } catch (IllegalArgumentException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addConstraintViolation();
            return false;
        }
    }
}
