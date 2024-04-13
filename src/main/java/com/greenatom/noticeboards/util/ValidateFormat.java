package com.greenatom.noticeboards.util;


import com.greenatom.noticeboards.model.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class ValidateFormat implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return MessageDto.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        /*RegionsDTO inputString = (RegionsDTO) o;
        if (inputString.getRegions() == null){
            errors.rejectValue("regions","404","Ошибка: Регион не должен быть пустым.");
        }else {
            try {
                List<Integer> elements = parserRequestBody.parseRequestBody(inputString.getRegions());
                for (Integer num : elements) {
                    if (num < 1 || num > 99) {
                        errors.rejectValue("regions","404","Ошибка: Регион должен быть в диапазоне от 1 по 99");
                    }
                }
            } catch (NumberFormatException e) {
                errors.rejectValue("regions","404","Ошибка: Неверный формат числа");
            }
        }
        if (errors.hasErrors()){
            errors.rejectValue("regions","404","Format examples: 13 или 13,14,77 или 1-3,6-8,98 или 1-99");
        }*/
    }
}
