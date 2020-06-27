package portfolio.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import portfolio.spring.boot.model.Company;

//Spring Data JPAのリポジトリにはメソッドの自動実装という機能がある
//リポジトリインターフェースに以下のようなルールでメソッドを定義しておくと、起動時に対応する検索を行うメソッドが自動的に作られる
	//メソッド名はfindByで始める
	//戻り値の型がListであれば該当するエンティティ全件、エンティティクラスであれば初めの一件が返される。
	//findByに続けて検索したいフィールドを書く。例えば以下のように定義すると「titleで検索するメソッド（完全一致）」が作られる
	//List<Offer> findByTitle(String title)
	//部分一致を行いたい場合はフィールド名の後ろに「Contains」を付ける（他にも前方一致などがある）
	//List<Offer> findByTitleContains(String title)
	//引数がエンティティの場合はIDが一致するかで検索される
	//List<Entry> findByOffer(Offer offer)
	//booleanについては直接「フィールド○○がtrueのもの」とメソッド名で指定できる（引数での切り替えも可能）
	//List<Offer> findByActiveTrue()
	//複数のフィールドの条件指定を「全部満たす」ものを検索したい場合は「And」でつなげる。
	//List<Offer> findByActiveTrueAndTitleContains(String title)

                                               //↓継承を記述。<対象テーブル,IDの型>
public interface CompanyRepository  extends JpaRepository<Company, Integer>{

}