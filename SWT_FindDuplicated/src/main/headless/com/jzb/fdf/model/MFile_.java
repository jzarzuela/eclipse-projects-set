package com.jzb.fdf.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-09-11T11:07:40.087+0200")
@StaticMetamodel(MFile.class)
public class MFile_ {
	public static volatile SingularAttribute<MFile, MFolder> folder;
	public static volatile SingularAttribute<MFile, String> hashing;
	public static volatile SingularAttribute<MFile, Long> id;
	public static volatile SingularAttribute<MFile, Boolean> isDuplicated;
	public static volatile SingularAttribute<MFile, Long> lastModified;
	public static volatile SingularAttribute<MFile, Long> lengh;
	public static volatile SingularAttribute<MFile, String> name;
	public static volatile SingularAttribute<MFile, Long> version;
}
