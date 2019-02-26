package com.jtudy.git.measure.domain;

import org.eclipse.jgit.lib.PersonIdent;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(of="email")
public class Author implements Comparable<Author>{
	
	private String name;
	private String email;
	
	public Author(PersonIdent personIdent) {
		name = personIdent.getName();
		email = personIdent.getEmailAddress();
	}

	@Override
	public int compareTo(Author o) {
		return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
	}
}
