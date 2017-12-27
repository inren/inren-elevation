package de.inren.data.domain.banking;

import org.apache.commons.lang.StringUtils;

public class CategoryFilterDto {

	private String categoryName;
	private String accountingTextFilter;
	private String principalFilter;
	private String purposeFilter;

	public CategoryFilterDto(String categoryName, String accountingTextFilter, String principalFilter,
			String purposeFilter) {
		this.categoryName = categoryName;
		this.accountingTextFilter = accountingTextFilter;
		this.principalFilter = principalFilter;
		this.purposeFilter = purposeFilter;
	}

	public CategoryFilterDto(CategoryFilter categoryFilter) {
		this.categoryName = categoryFilter.getCategory().getName();
		this.accountingTextFilter = categoryFilter.getAccountingTextFilter();
		this.principalFilter = categoryFilter.getPrincipalFilter();
		this.purposeFilter = categoryFilter.getPurposeFilter();
	}

	public boolean matches(CategoryFilter categoryFilter) {
		return StringUtils.equals(this.categoryName, categoryFilter.getCategory().getName())
				&& StringUtils.equals(this.accountingTextFilter, categoryFilter.getAccountingTextFilter())
				&& StringUtils.equals(this.principalFilter, categoryFilter.getPrincipalFilter())
				&& StringUtils.equals(this.purposeFilter, categoryFilter.getPurposeFilter());
	}

	public CategoryFilter toCategoryFilter(Category category) {
		return new CategoryFilter(category, accountingTextFilter, principalFilter, purposeFilter);
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String category) {
		this.categoryName = category;
	}

	public String getAccountingTextFilter() {
		return accountingTextFilter;
	}

	public void setAccountingTextFilter(String accountingTextFilter) {
		this.accountingTextFilter = accountingTextFilter;
	}

	public String getPrincipalFilter() {
		return principalFilter;
	}

	public void setPrincipalFilter(String principalFilter) {
		this.principalFilter = principalFilter;
	}

	public String getPurposeFilter() {
		return purposeFilter;
	}

	public void setPurposeFilter(String purposeFilter) {
		this.purposeFilter = purposeFilter;
	}

	@Override
	public String toString() {
		return "CategoryFilterDto [categoryName=" + categoryName + ", accountingTextFilter=" + accountingTextFilter
				+ ", principalFilter=" + principalFilter + ", purposeFilter=" + purposeFilter + "]";
	}

}
