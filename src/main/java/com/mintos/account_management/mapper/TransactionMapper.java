package com.mintos.account_management.mapper;

import com.mintos.account_management.dto.TransactionDto;
import com.mintos.account_management.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {

    TransactionDto mapTransactionToTransactionDto(Transaction transaction);

}