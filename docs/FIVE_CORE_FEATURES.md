# CardVision AI — Beş Zorunlu Ana Özellik

Bu belge kullanıcı tarafından onaylanan **beş özellik grubunu** CardVision AI için bağlayıcı ürün kapsamı olarak tanımlar. Özelliklerin burada tanımlanması, uygulandıkları anlamına gelmez. Mevcut özellikler silinmeyecek. Kullanıcı istemeden APK/AAB üretilmeyecek.

## 1. Gerçek AI kart tanıma
- Kamera ve galeri, ön/arka yüz, toplu tarama, kart sınırlarını bulma, kırpma, perspektif düzeltme, bulanıklık/parlama uyarısı.
- OCR + görsel eşleştirme; spor/TCG, oyuncu, takım, marka, yıl, set, kart numarası, paralel/varyant, seri numarası, imza, rookie, dereceli kart.
- Güven puanı, birden fazla aday gösterme, kullanıcı doğrulaması ve elle düzeltme; eşleşme bulunamadığında yanlış kesin sonuç üretmeme.
- Gerekli: lisanslı katalog, model/veri hattı, cihaz ve sunucu performans testleri.
- Kabul: yanlış eşleşmeler güvenle reddedilir, düzeltme kaydedilir, tarama geçmişi saklanır.

## 2. Milyonlarca kart kataloğu ve gerçek piyasa fiyatları
- Spor kartları ve Pokémon/MTG/Yu-Gi-Oh! dahil genişletilebilir katalog; setler, paraleller, numaralar, baskılar, dereceler.
- Yetkili sağlayıcılardan gerçek tamamlanmış satış karşılaştırmaları; fiyat, tarih, kondisyon, grade, para birimi, kaynak bağlantısı.
- 7/30/90/365 günlük grafik, medyan fiyat, aykırı satışları ayırma, veri yoksa dürüst boş durum.
- Koleksiyon toplam değeri, maliyet, kâr/zarar, izleme listesi, fiyat uyarıları.
- Gerekli: veri lisansı, API anlaşmaları, güncelleme işleri ve para birimi dönüşümü.
- Kabul: canlı olmayan fiyatlar 'canlı' diye gösterilmez; her değer için tarih ve kaynak belirtilir.

## 3. Tam pazaryeri
- Kart ilanı: fotoğraf, kondisyon, varyant, fiyat, kargo, satıcı profili.
- Arama, favori ilan, teklif/karşı teklif, toplu teklifler, alıcı-satıcı sohbeti.
- Sipariş, ödeme, komisyon, kargo takip, iade/itiraz, sahtecilik önleme, şikâyet ve moderasyon.
- Yetkili ödeme altyapısı, satıcı doğrulama, ülke bazlı vergi/tüketici mevzuatı.
- Kabul: ödeme ve sipariş akışı güvenlik ve mevzuat incelemesi olmadan yayına alınmaz; sahte aktif ilan gösterilmez.

## 4. Profesyonel koleksiyon yönetimi
- Kart ön/arka fotoğrafları, birden çok kopya, miktar, not, etiket, klasör, binder, set kontrol listeleri.
- Gelişmiş arama/filtre/sıralama, toplu düzenleme, favori, istek listesi, takas listesi.
- Alış fiyatı, alış tarihi, kondisyon, grading bilgisi, portföy grafikleri, dışa/içe aktar CSV ve JSON.
- Bulut senkronizasyonu, offline-first, yedekleme/geri yükleme, çakışma yönetimi.
- Kabul: yedek geri yükleme mevcut kayıtları kaybetmeden çalışır; migration testleri geçer.

## 5. Sosyal topluluk, kullanıcı hesabı ve Premium
- E-posta/Google ile hesap, profil fotoğrafı, kullanıcı adı, hesap silme ve veri dışa aktarma.
- Takip, gönderi, beğeni, yorum, paylaşım, koleksiyon görünürlüğü, bildirimler.
- AI kart danışmanı: katalog ve satış verisine dayalı, kaynaklı ve belirsizlik belirten yanıtlar.
- Ücretsiz/Pro hakları, Google Play Billing, satın alma doğrulaması, geri yükleme ve abonelik yönetimi.
- Moderasyon, engelleme/şikâyet, gizlilik, yaş ve güvenlik kuralları.
- Kabul: ücretlendirme doğrulanmadan kullanıcıdan ödeme alınmaz.

## Tasarım ve uygulama ilkeleri
- Özgün CardVision AI kimliği; beyaz/açık gri/mavi premium arayüz, isteğe bağlı koyu tema.
- Beş ana sekme: Ana Sayfa, Tara, Koleksiyon, Piyasa, Profil.
- Mevcut kart ekleme, OCR, yerel kayıt, klasör, favori, düzenleme, maliyet/portföy ve JSON yedek işlevleri korunacak.
- Her özelliğe: veri modeli, gerçek çalışan ekran, hata/boş/yükleniyor durumları, güvenlik, test, kabul kriteri.
- API/lisans/ödeme entegrasyonu olmadan sahte 'canlı fiyat', 'gerçek AI', 'ödeme tamamlandı' iddiası yok.
- APK/AAB oluşturma ve yayımlama kullanıcı ayrıca istediğinde yapılacak.

## Geliştirme sırası
1. Veri modelleri ve mevcut koleksiyon ekranlarının sağlamlaştırılması.
2. Kamera + tarama doğrulama + katalog eşleştirme.
3. Katalog ve lisanslı satış fiyatı adaptörleri.
4. Gelişmiş koleksiyon, setler, toplu işlemler, içe/dışa aktar.
5. Hesap/bulut ve sosyal topluluk.
6. Pazaryeri, güvenli ödeme/kargo ve moderasyon.
7. Premium ve uçtan uca test.
