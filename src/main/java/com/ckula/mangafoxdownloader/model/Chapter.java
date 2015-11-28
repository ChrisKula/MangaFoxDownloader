package com.ckula.mangafoxdownloader.model;

public class Chapter implements Comparable<Chapter> {

	private String name = "";
	private String chapterNumber = "";
	private int pagesCount = 0;

	private String link = "";
	private String associatedVolume = "";

	public Chapter() {

	}

	public Chapter(String name, int pagesCount) {
		this.name = name;
		this.pagesCount = pagesCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPagesCount() {
		return pagesCount;
	}

	public void setPagesCount(int pagesCount) {
		this.pagesCount = pagesCount;
	}

	public String getChapterNumber() {
		return chapterNumber;
	}

	public void setChapterNumber(String chapterNumber) {
		this.chapterNumber = chapterNumber;
	}

	public String getAssociatedVolume() {
		return associatedVolume;
	}

	public void setAssociatedVolume(String associatedVolume) {
		this.associatedVolume = associatedVolume;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "Chapter [name=" + name + ", chapterNumber=" + chapterNumber + ", pagesCount=" + pagesCount + ", link="
				+ link + ", associatedVolume=" + associatedVolume + "]";
	}

	@Override
	public int compareTo(Chapter chap) {
		Double thisChapterNumber;
		Double chapNumber;
		try {
			thisChapterNumber = Double.valueOf(this.chapterNumber);

		} catch (java.lang.NumberFormatException e) {
			return 1;
		}

		try {
			chapNumber = Double.valueOf(chap.getChapterNumber());
		} catch (java.lang.NumberFormatException e) {
			return -1;
		}

		return thisChapterNumber.intValue() - chapNumber.intValue();
	}

}