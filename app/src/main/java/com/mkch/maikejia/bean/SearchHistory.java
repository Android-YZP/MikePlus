package com.mkch.maikejia.bean;

import java.util.List;

public class SearchHistory {
	private List<String> historyNames;

	public List<String> getHistoryNames() {
		return historyNames;
	}

	public void setHistoryNames(List<String> historyNames) {
		this.historyNames = historyNames;
	}

	public SearchHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SearchHistory(List<String> historyNames) {
		super();
		this.historyNames = historyNames;
	}

	@Override
	public String toString() {
		return "SearchHistory [historyNames=" + historyNames + "]";
	}

	
}
