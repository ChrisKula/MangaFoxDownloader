package com.ckula.mangafoxdownloader.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Chapter implements Comparable<Chapter> {

    @Expose
    @SerializedName("volume")
    private String associatedVolume = "";

    @Expose
    @SerializedName("chapter")
    private String chapterNumber = "";

    private int pagesCount = 0;
    private String link = "";

    public Chapter() {

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
	return "Chapter [chapterNumber=" + chapterNumber + ", pagesCount=" + pagesCount + ", link=" + link
		+ ", associatedVolume=" + associatedVolume + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((associatedVolume == null) ? 0 : associatedVolume.hashCode());
	result = prime * result + ((chapterNumber == null) ? 0 : chapterNumber.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof Chapter)) {
	    return false;
	}
	Chapter other = (Chapter) obj;
	if (associatedVolume == null) {
	    if (other.associatedVolume != null) {
		return false;
	    }
	} else if (!associatedVolume.equalsIgnoreCase(other.associatedVolume)) {
	    return false;
	}
	if (chapterNumber == null) {
	    if (other.chapterNumber != null) {
		return false;
	    }
	} else if (!chapterNumber.equalsIgnoreCase(other.chapterNumber)) {
	    return false;
	}
	return true;
    }

    @Override
    public int compareTo(Chapter otherChapitre) {

	double thisValue = 0.0;
	double otherValue = 0.0;

	if ("NA".equals(this.associatedVolume)) {
	    thisValue = Double.MAX_VALUE;
	} else if ("TBD".equals(this.associatedVolume)) {
	    thisValue = Double.MAX_VALUE - 1;
	} else {
	    thisValue = Double.valueOf(this.associatedVolume);
	}

	if ("NA".equals(otherChapitre.associatedVolume)) {
	    otherValue = Double.MAX_VALUE;
	} else if ("TBD".equals(otherChapitre.associatedVolume)) {
	    otherValue = Double.MAX_VALUE - 1;
	} else {
	    otherValue = Double.valueOf(otherChapitre.associatedVolume);
	}

	int diff = 0;
	if ((thisValue - otherValue) > 0) {
	    diff = 1;
	} else if ((thisValue - otherValue) < 0) {
	    diff = -1;
	}

	if (diff == 0) {
	    diff = 0;
	    thisValue = Double.valueOf(this.chapterNumber);
	    otherValue = Double.valueOf(otherChapitre.chapterNumber);

	    if ((thisValue - otherValue) > 0) {
		diff = 1;
	    } else if ((thisValue - otherValue) < 0) {
		diff = -1;
	    }
	}
	
	return diff;
    }
}