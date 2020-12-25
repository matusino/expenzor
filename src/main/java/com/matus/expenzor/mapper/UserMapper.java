package com.matus.expenzor.mapper;

import com.matus.expenzor.dto.user.UserDTO;
import com.matus.expenzor.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, uses = {ExpenseMapper.class}, componentModel = "spring")
public interface UserMapper {


    User dtoToUser(final UserDTO userDTO);

    UserDTO userToDto(final User user);

}
