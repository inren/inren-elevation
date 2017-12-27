package de.inren.data.domain.banking;

public class CategoryDto {

	private String name;

	private boolean income;

	private boolean marksMonth;

	private String description;

	public CategoryDto(String name, boolean income, boolean marksMonth, String description) {
		this.name = name;
		this.income = income;
		this.marksMonth = marksMonth;
		this.description = description;
	}

	public CategoryDto(Category category) {
		this.name = category.getName();
		this.income = category.isIncome();
		this.marksMonth = category.isMarksMonth();
		this.description = category.getDescription();
	}

	public Category toCategory() {
		return new Category(name, income, marksMonth, description);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isIncome() {
		return income;
	}

	public void setIncome(boolean income) {
		this.income = income;
	}

	public boolean isMarksMonth() {
		return marksMonth;
	}

	public void setMarksMonth(boolean marksMonth) {
		this.marksMonth = marksMonth;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "CategoryDto [name=" + name + ", income=" + income + ", marksMonth=" + marksMonth + ", description="
				+ description + "]";
	}

}
