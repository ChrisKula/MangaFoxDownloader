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
    public int compareTo(Chapter otherChapitre) {
	Double thisChapterAssociatedVolume;
	Double otherChapterAssociatedVolume;

	boolean isThisAssociatedVolumeANumber = true;
	boolean isOtherAssociatedVolumeANumber = true;

	try {
	    thisChapterAssociatedVolume = Double.parseDouble(this.getAssociatedVolume());
	} catch (NumberFormatException e) {
	    isThisAssociatedVolumeANumber = false;
	    thisChapterAssociatedVolume = 0.0;
	}

	try {
	    otherChapterAssociatedVolume = Double.parseDouble(otherChapitre.getAssociatedVolume());
	} catch (NumberFormatException e) {
	    isOtherAssociatedVolumeANumber = false;
	    otherChapterAssociatedVolume = 0.0;
	}

	if (isThisAssociatedVolumeANumber && isOtherAssociatedVolumeANumber) {
	    int diff = thisChapterAssociatedVolume.intValue() - otherChapterAssociatedVolume.intValue();
	    if (diff == 0) {
		Double thisChapterNumber;
		Double otherChapNumber;
		try {
		    thisChapterNumber = Double.parseDouble(this.getChapterNumber());
		} catch (NumberFormatException e) {
		    return 1;
		}

		try {
		    otherChapNumber = Double.parseDouble(otherChapitre.getChapterNumber());
		} catch (NumberFormatException e) {
		    return -1;
		}

		return thisChapterNumber.intValue() - otherChapNumber.intValue();
	    } else {
		return diff;
	    }

	} else if (isThisAssociatedVolumeANumber && !isOtherAssociatedVolumeANumber) {
	    return -1;
	} else if (!isThisAssociatedVolumeANumber && isOtherAssociatedVolumeANumber) {
	    return 1;
	} else {
	    int diff = this.getAssociatedVolume().compareToIgnoreCase(otherChapitre.getAssociatedVolume());
	    if (diff == 0) {
		Double thisChapterNumber;
		Double otherChapNumber;
		try {
		    thisChapterNumber = Double.parseDouble(this.getChapterNumber());
		} catch (NumberFormatException e) {
		    return 1;
		}

		try {
		    otherChapNumber = Double.parseDouble(otherChapitre.getChapterNumber());
		} catch (NumberFormatException e) {
		    return -1;
		}

		return thisChapterNumber.intValue() - otherChapNumber.intValue();
	    } else {
		return diff;
	    }

	}
    }

}