package de.inren.data.repositories.banking;

import org.springframework.data.repository.PagingAndSortingRepository;

import de.inren.data.domain.banking.Account;
import de.inren.data.domain.banking.Token;

public interface TokenRepository extends PagingAndSortingRepository<Token, Long> {

	Account findByValue(String value);
}
