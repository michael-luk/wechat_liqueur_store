package models;

import LyLib.Interfaces.IConst;
import LyLib.Utils.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import play.db.ebean.Model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "catalogs")
public class CatalogModel extends Model implements Serializable, IConst {

	@Id
	public Long id;

	public int catalogIndex;// 顺序

	public String name; // 名称

	public String images; // 图片

	public String smallImages; // 小图片

	public String wishWord; // 主题下的祝福语

	@Lob
	public String description1;

	@Lob
	public String description2;

	public String comment;

	@JsonIgnore
	@ManyToMany(targetEntity = models.ProductModel.class)
	public List<ProductModel> products; // 分类下产品

	public static Finder<Long, CatalogModel> find = new Finder(Long.class, CatalogModel.class);

	/**
	 * Retrieve all .
	 */
	public static List<CatalogModel> findAll() {
		return find.all();
	}

	public String validate() {
		if (StrUtil.isNull(name)) {
			return "必须要有名称或标题";
		}
		if (StrUtil.isNull(wishWord)) {
			return "必须要有祝福语";
		}
		if (StrUtil.isNull(description1)) {
			return "必须要有主题描述";
		}
		return null;
	}

	@Override
	public String toString() {
		return "Catalog [name:" + name + "]";
	}
}