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
@Table(name = "themes")
public class ThemeModel extends Model implements Serializable, IConst {

	@Id
	public Long id;

	public String name; // 名称

	@Lob
	public String images; // 图片

	public Long refProductId; // 所属产品的ID

	// @Required
	@JsonIgnore
	@ManyToOne
	public ProductModel product;// 所属的产品

	public String comment;

	public static Finder<Long, ThemeModel> find = new Finder(Long.class, ThemeModel.class);

	/**
	 * Retrieve all .
	 */
	public static List<ThemeModel> findAll() {
		return find.all();
	}

	public String validate() {
		if (StrUtil.isNull(name)) {
			return "必须要有名称";
		}
		// if (product == null) {
		// return "必须要有所属的产品";
		// }
		return null;
	}

	@Override
	public String toString() {
		return "Theme [name:" + name + "]";
	}
}