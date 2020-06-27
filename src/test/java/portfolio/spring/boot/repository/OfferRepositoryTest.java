package portfolio.spring.boot.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import portfolio.spring.boot.model.Offer;


//リポジトリインターフェースのテストを行うにはテストクラスに@DataJpaTestアノテーションを付ける。
//@DataJpaTestアノテーションを付けることでテスト用のデータベースが設定される。
//テスト用のデータベースはデフォルトではH2というインメモリデータベース（データをメモリ上だけで管理する。そのためプログラムが終了すると消える）が使われる。


@DataJpaTest
public class OfferRepositoryTest {
	
	//TestEntityManagerはJPA仕様で定義されているEntityManagerと同じようにリポジトリを使わずにエンティティの操作を行うクラス
	@Autowired
	private TestEntityManager entityManager;
	
	
	@Autowired
	private OfferRepository offerRepository;
	
	@BeforeEach
	public void setUp() {
		
		//テストに使用するオブジェクトを作成する
		Offer offer1 = new Offer();
		offer1.setTitle("清掃ボランティア募集");
		offer1.setPrefecture("東京都");
		offer1.setAddress("新宿区");
		offer1.setActive(true);	
		//TestEntityManagerのpersistメソッドを使用しオブジェクトを保存する
		entityManager.persist(offer1);

		Offer offer2 = new Offer();
		offer2.setTitle("シニアボランティア募集");
		offer2.setPrefecture("大阪府");
		offer2.setAddress("大阪市");
		offer2.setActive(true);
		entityManager.persist(offer2);

		Offer offer3 = new Offer();
		offer3.setTitle("子ども見守りボランティア募集");
		offer3.setPrefecture("愛知県");
		offer3.setAddress("名古屋市");
		offer3.setActive(false);
		entityManager.persist(offer3);

		Offer offer4 = new Offer();
		offer4.setTitle("シニアボランティア募集");
		offer4.setPrefecture("福岡県");
		offer4.setAddress("福岡市");
		offer4.setActive(true);
		entityManager.persist(offer4);
	}
	
	//リポジトリインターフェースを使い、期待通りの検索が行われるかをテストする

	
	@Test
	public void testFindByActive() {
		List<Offer> offers = offerRepository.findByActiveTrue();
		assertThat(offers.size()).isEqualTo(3);
	}

	@Test
	public void testFindByActiveAndTitle() {
		List<Offer> offers = offerRepository.findByActiveTrueAndTitleContainsAndPrefectureContains("シニア", "");
		assertThat(offers.size()).isEqualTo(2);
	}

	@Test
	public void testFindByActiveAndPrefecture() {
		List<Offer> offers = offerRepository.findByActiveTrueAndTitleContainsAndPrefectureContains("", "東京都");
		assertThat(offers.size()).isEqualTo(1);
	}

	@Test
	public void testFindByActiveAndTitleAndPrefecture() {
		List<Offer> offers = offerRepository.findByActiveTrueAndTitleContainsAndPrefectureContains("シニア", "大阪府");
		assertThat(offers.size()).isEqualTo(1);
	}
}