package com.mintos.account_management.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "am_clients")
public class Client extends BaseEntity {

    @OneToMany(mappedBy = "client")
    private List<Account> accounts;

    private String name;

}
