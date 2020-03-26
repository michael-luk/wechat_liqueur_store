package models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import LyLib.Interfaces.IConst;
import LyLib.Utils.DateUtil;
import LyLib.Utils.StrUtil;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
@Table(name = "winebodys")
public class WineBodyModel extends Model implements Serializable, IConst {

	@Id
	public Long id;

	public String name; // 名称

	public double price; // 价格

	public double resellerMark; // 分销额

	public String comment;

	public static Finder<Long, WineBodyModel> find = new Finder(Long.class, WineBodyModel.class);

	/**
	 * Retrieve all .
	 */
	public static List<WineBodyModel> findAll() {
		return find.all();
	}

	public String validate() {
		if (StrUtil.isNull(name)) {
			return "必须要有名称";
		}
		return null;
	}

	@Override
	public String toString() {
		return "WineBody [name:" + name + "]";
	}
}