package com.mintos.account_management.mapper;

import com.mintos.account_management.dto.AccountDto;
import com.mintos.account_management.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    AccountDto mapAccountToAccountDto(Account account);

}