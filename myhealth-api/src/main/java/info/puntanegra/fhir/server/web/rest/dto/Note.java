package info.puntanegra.fhir.server.web.rest.dto;

import java.util.Date;

public class Note {
	private String title;
	private String text;
	private Date date;

	public Note() {

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
