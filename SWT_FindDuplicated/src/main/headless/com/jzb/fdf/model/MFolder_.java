package com.jzb.fdf.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-09-28T20:11:54.888+0200")
@StaticMetamodel(MFolder.class)
public class MFolder_ {
	public static volatile SingularAttribute<MFolder, Integer> duplicatedFilesCount;
	public static volatile SingularAttribute<MFolder, Integer> duplicatedSubfoldersCount;
	public static volatile MapAttribute<MFolder, String, MFile> files;
	public static volatile SingularAttribute<MFolder, String> fullName;
	public static volatile SingularAttribute<MFolder, Long> id;
	public static volatile SingularAttribute<MFolder, String> name;
	public static volatile SingularAttribute<MFolder, MFolder> parent;
	public static volatile MapAttribute<MFolder, String, MFolder> subFolders;
	public static volatile SingularAttribute<MFolder, Long> version;
}
