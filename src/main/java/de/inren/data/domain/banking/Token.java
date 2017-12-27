package de.inren.data.domain.banking;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import de.inren.data.domain.core.DomainObject;

@Entity(name = "Token")
public class Token extends DomainObject {

	@Column(nullable = false, unique = true)
	private String value;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "token_transactions", joinColumns = {
			@JoinColumn(name = "token_transactions_tokenid", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "token_transactionid", referencedColumnName = "id") })
	private Set<Transaction> transactions;

	@Lob
	@Column(columnDefinition = "TEXT", length = 65535)
	private String infoSrc;

	@Column()
	private Integer infoMatches;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Set<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}

	public String getInfoSrc() {
		return infoSrc;
	}

	public void setInfoSrc(String infoSrc) {
		this.infoSrc = infoSrc;
	}

	public Integer getInfoMatches() {
		return infoMatches;
	}

	public void setInfoMatches(Integer infoMatches) {
		this.infoMatches = infoMatches;
	}

	@Override
	public String toString() {
		return "Token [value=" + value + ", transactions=" + transactions + "]";
	}

}
