package com.cardvision.ai;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.*;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import androidx.core.content.FileProvider;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.util.*;

public class MainActivity extends Activity {
  LinearLayout root, content;
  JSONArray cards;
  int blue=Color.rgb(38,99,235), ink=Color.rgb(22,34,57); boolean dark=false;
  android.content.SharedPreferences prefs;
  String pendingPhoto="";
  String cameraPhoto="";
  boolean batchMode=false;
  @Override public void onCreate(Bundle b){super.onCreate(b);prefs=getSharedPreferences("cards",MODE_PRIVATE);load();dark=prefs.getBoolean("dark",false);home();}
  void load(){try{cards=new JSONArray(prefs.getString("items","[]"));}catch(Exception e){cards=new JSONArray();}}
  void save(){prefs.edit().putString("items",cards.toString()).apply();}
  int surface(){return dark?Color.rgb(24,34,52):Color.WHITE;}
  int background(){return dark?Color.rgb(12,19,32):Color.rgb(246,248,253);}
  int foreground(){return dark?Color.WHITE:ink;}
  GradientDrawable shape(int color,int radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(radius);return g;}
  TextView label(String s,int size,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(foreground());t.setPadding(10,12,10,12);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
  void screen(String title){root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(background());setContentView(root);TextView h=label(title,27,true);h.setPadding(24,28,24,18);root.addView(h);ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);content=new LinearLayout(this);content.setPadding(20,8,20,24);content.setOrientation(1);scroll.addView(content);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));LinearLayout nav=new LinearLayout(this);nav.setPadding(6,8,6,8);nav.setBackgroundColor(surface());String[] names={"Ana","Tara","Kartlar","Piyasa","Profil"};Runnable[] actions={()->home(),()->scan(),()->collection(""),()->market(),()->profile()};for(int i=0;i<names.length;i++){final int idx=i;TextView tab=label(names[i],12,false);tab.setGravity(Gravity.CENTER);tab.setTextColor(blue);nav.addView(tab,new LinearLayout.LayoutParams(0,-2,1));tab.setOnClickListener(v->actions[idx].run());}root.addView(nav);}
  void button(String title,Runnable action){TextView b=label(title,16,true);b.setTextColor(blue);b.setPadding(20,18,20,18);b.setBackground(shape(surface(),22));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=12;content.addView(b,p);b.setOnClickListener(v->action.run());}
  void stat(String name,String value){LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(20,14,20,14);card.setBackground(shape(surface(),24));card.addView(label(name,13,false));card.addView(label(value,23,true));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=12;content.addView(card,p);}
  EditText field(String hint){EditText e=new EditText(this);e.setHint(hint);content.addView(e);return e;}
  void message(String text){new AlertDialog.Builder(this).setMessage(text).setPositiveButton("Tamam",null).show();}
  void home(){screen("CardVision AI");content.addView(label("Kartlarını keşfet, koleksiyonunu yönet",16,false));stat("Koleksiyonumdaki kartlar",String.valueOf(cards.length()));button("📷 Kart tara (OCR)",()->scan());button("＋ Kart ekle",()->add());button("▦ Koleksiyonum ("+cards.length()+")",()->collection(""));button("⌕ Kart ara",()->search());button("▤ Klasörler / etiketler",()->folders());button("✓ Set kontrol listesi",()->checklists());button("♡ İstek listem",()->wishlist());button("↧ JSON yedek içe aktar",()->importBackup());button("☆ Favoriler",()->favorites());button("◷ Portföy değeri",()->portfolio());button("⇧ Koleksiyonu paylaş",()->export());button("CSV dışa aktar",()->exportCsv());button("📊 Koleksiyon istatistikleri",()->collectionStats());button("Satış ilanı taslaklarım",()->listings());button("⚙ Entegrasyon durumu",()->status());button(dark?"☀ Açık tema":"☾ Koyu tema",()->{dark=!dark;prefs.edit().putBoolean("dark",dark).apply();home();});}
  void scan(){batchMode=false;scanScreen();}
  void scanScreen(){pendingPhoto="";screen(batchMode?"Toplu kart tarama":"Kart tara");content.addView(label("Kamerayla çek veya galeriden fotoğraf seç. Cihaz üzerinde OCR yapılır; kart eşleşmesi henüz doğrulanmaz.",16,false));button("Kamera ile çek",()->{Intent camera=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);try{File dir=new File(getFilesDir(),"card_photos");if(!dir.exists()&&!dir.mkdirs())throw new IOException("Klasör oluşturulamadı");File file=new File(dir,UUID.randomUUID()+".jpg");cameraPhoto=file.getAbsolutePath();Uri output=FileProvider.getUriForFile(this,"com.cardvision.ai.fileprovider",file);camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,output);camera.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivityForResult(camera,402);}catch(Exception e){cameraPhoto="";message("Kamera açılamadı: "+e.getMessage());}});button("Fotoğraf seç",()->{Intent i=new Intent(Intent.ACTION_GET_CONTENT);i.setType("image/*");startActivityForResult(Intent.createChooser(i,"Kart fotoğrafı"),401);});button(batchMode?"Toplu taramayı bitir":"⇄ Toplu tarama moduna geç",()->{if(batchMode){batchMode=false;home();}else{batchMode=true;scanScreen();}});button("← Ana sayfa",()->{batchMode=false;home();});}
  @Override protected void onActivityResult(int request,int result,Intent data){
    super.onActivityResult(request,result,data);
    if(result!=RESULT_OK){pendingPhoto="";cameraPhoto="";return;}
    try{
      InputImage image;
      if(request==401){
        Uri uri=data==null?null:data.getData();
        if(uri==null){pendingPhoto="";message("Fotoğraf seçilmedi");return;}
        pendingPhoto=copyCardPhoto(uri);image=InputImage.fromFilePath(this,uri);
      }else if(request==402){
        if(cameraPhoto.isEmpty()||!new File(cameraPhoto).exists()||new File(cameraPhoto).length()==0){message("Kamera fotoğrafı alınamadı");cameraPhoto="";return;}
        pendingPhoto=cameraPhoto;cameraPhoto="";
        image=InputImage.fromFilePath(this,Uri.fromFile(new File(pendingPhoto)));
      }else return;
      com.google.mlkit.vision.text.TextRecognizer recognizer=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
      recognizer.process(image).addOnSuccessListener(resultText->{
        String value=resultText.getText();
        screen("Okunan kart yazıları");
        content.addView(label(value.isEmpty()?"Yazı bulunamadı. Daha net bir fotoğraf dene.":"OCR sonucu:\\n"+value,16,false));
        if(!value.trim().isEmpty())button("Kartı koleksiyona ekle",()->addFromScan(value));
        button("← Tekrar tara",()->scan());
      }).addOnFailureListener(err->{pendingPhoto="";message("OCR hatası: "+err.getMessage());})
        .addOnCompleteListener(task->recognizer.close());
    }catch(Exception e){pendingPhoto="";message("Fotoğraf işlenemedi: "+e.getMessage());}
  }
  String copyCardPhoto(Uri uri)throws IOException{
    File dir=new File(getFilesDir(),"card_photos");if(!dir.exists()&&!dir.mkdirs())throw new IOException("Fotoğraf klasörü oluşturulamadı");
    File dest=new File(dir,UUID.randomUUID().toString()+".jpg");
    try(InputStream in=getContentResolver().openInputStream(uri);OutputStream out=new FileOutputStream(dest)){
      if(in==null)throw new IOException("Fotoğraf okunamadı");
      byte[] buf=new byte[8192];int n;long total=0;
      while((n=in.read(buf))!=-1){total+=n;if(total>20*1024*1024)throw new IOException("Fotoğraf 20 MB sınırını aşıyor");out.write(buf,0,n);}
    }catch(IOException e){dest.delete();throw e;}
    return dest.getAbsolutePath();
  }
  String saveCardBitmap(Bitmap bmp)throws IOException{
    File dir=new File(getFilesDir(),"card_photos");if(!dir.exists()&&!dir.mkdirs())throw new IOException("Fotoğraf klasörü oluşturulamadı");
    File dest=new File(dir,UUID.randomUUID().toString()+".jpg");
    try(OutputStream out=new FileOutputStream(dest)){if(!bmp.compress(Bitmap.CompressFormat.JPEG,90,out))throw new IOException("Fotoğraf kaydedilemedi");}
    return dest.getAbsolutePath();
  }
  void showCardPhoto(JSONObject card){
    String path=card.optString("photo");if(path.isEmpty())return;
    File file=new File(path);if(!file.exists())return;
    BitmapFactory.Options opts=new BitmapFactory.Options();opts.inJustDecodeBounds=true;BitmapFactory.decodeFile(path,opts);
    opts.inSampleSize=1;while(opts.outWidth/opts.inSampleSize>800||opts.outHeight/opts.inSampleSize>800)opts.inSampleSize*=2;
    opts.inJustDecodeBounds=false;Bitmap bmp=BitmapFactory.decodeFile(path,opts);
    if(bmp!=null){ImageView view=new ImageView(this);view.setImageBitmap(bmp);view.setAdjustViewBounds(true);view.setMaxHeight(500);content.addView(view);}
  }
  void addFromScan(String value){screen("Taramadan kart ekle");EditText name=field("Kart adı / oyuncu"),sport=field("Spor veya TCG"),brand=field("Marka / set"),year=field("Yıl"),number=field("Kart numarası"),price=field("Tahmini değer (USD)");CardTextParser parsed=CardTextParser.parse(value);name.setText(parsed.name);sport.setText(parsed.category.equals("Belirsiz")?"":parsed.category);brand.setText(parsed.brand);year.setText(parsed.year);number.setText(parsed.number);content.addView(label("OCR tahmini: Lütfen kart adını, yılı ve numarasını kontrol et. Katalog eşleşmesi doğrulanmadı.",14,false));button("Koleksiyona kaydet",()->{if(name.getText().toString().trim().isEmpty()){message("Kart adı gerekli");return;}try{JSONObject o=new JSONObject();o.put("id",UUID.randomUUID().toString());o.put("name",name.getText().toString().trim());o.put("sport",sport.getText().toString());o.put("brand",brand.getText().toString());o.put("year",year.getText().toString());o.put("number",number.getText().toString());o.put("price",price.getText().toString());o.put("ocr",value);o.put("photo",pendingPhoto);o.put("favorite",false);String key=o.optString("name").trim().toLowerCase(Locale.ROOT)+"|"+o.optString("year")+"|"+o.optString("number");boolean duplicate=false;for(int i=0;i<cards.length();i++){JSONObject old=cards.optJSONObject(i);if(old!=null&&(old.optString("name").trim().toLowerCase(Locale.ROOT)+"|"+old.optString("year")+"|"+old.optString("number")).equals(key)){duplicate=true;break;}}if(duplicate){new AlertDialog.Builder(this).setTitle("Benzer kart var").setMessage("Koleksiyonunda aynı isim, yıl ve numaraya sahip bir kart var. Yine de eklensin mi?").setNegativeButton("İptal",null).setPositiveButton("Ekle",(d,w)->{cards.put(o);save();afterScanSave();}).show();}else{cards.put(o);save();afterScanSave();}}catch(Exception e){message("Kaydedilemedi");}});}
  void afterScanSave(){pendingPhoto="";if(batchMode){message("Kart kaydedildi. Sıradaki kartı tara.");scanScreen();}else home();}
  void add(){screen("Yeni kart");EditText name=field("Kart / oyuncu adı"),sport=field("Spor veya TCG"),brand=field("Marka / set"),year=field("Yıl"),number=field("Kart numarası"),variant=field("Varyant / parallel"),price=field("Tahmini değer (USD)"),cost=field("Alış fiyatı (USD)"),folder=field("Klasör"),notes=field("Notlar"),grade=field("Kondisyon / grading");button("Kartı kaydet",()->{String n=name.getText().toString().trim();if(n.isEmpty()){message("Kart adı gerekli");return;}try{JSONObject o=new JSONObject();o.put("id",UUID.randomUUID().toString());o.put("name",n);o.put("sport",sport.getText().toString());o.put("brand",brand.getText().toString());o.put("year",year.getText().toString());o.put("number",number.getText().toString());o.put("variant",variant.getText().toString());o.put("price",price.getText().toString());o.put("cost",cost.getText().toString());o.put("folder",folder.getText().toString());o.put("notes",notes.getText().toString());o.put("grade",grade.getText().toString());o.put("favorite",false);cards.put(o);save();home();}catch(Exception e){message("Kaydedilemedi");}});button("← Geri",()->home());}
  void search(){screen("Gelişmiş kart arama");EditText q=field("Kart, oyuncu, marka, yıl, numara");EditText category=field("Spor / kategori (isteğe bağlı)");EditText folder=field("Klasör (isteğe bağlı)");button("Ara",()->collectionFiltered(q.getText().toString(),category.getText().toString(),folder.getText().toString(),"Ad (A-Z)"));button("← Ana sayfa",()->home());}
  void collection(String query){collectionFiltered(query,"","","Ad (A-Z)");}
  void collectionFiltered(String query,String category,String folder,String order){
    screen("Koleksiyonum");
    ArrayList<JSONObject> matches=new ArrayList<>();
    for(int i=0;i<cards.length();i++){
      JSONObject o=cards.optJSONObject(i);if(o==null)continue;
      String q=query.toLowerCase(Locale.ROOT),c=category.toLowerCase(Locale.ROOT),f=folder.toLowerCase(Locale.ROOT);
      if(!(o.optString("name")+" "+o.optString("brand")+" "+o.optString("year")+" "+o.optString("number")+" "+o.optString("variant")).toLowerCase(Locale.ROOT).contains(q))continue;
      if(!o.optString("sport").toLowerCase(Locale.ROOT).contains(c)||!o.optString("folder").toLowerCase(Locale.ROOT).contains(f))continue;
      matches.add(o);
    }
    Comparator<JSONObject> sorter=(x,y)->x.optString("name").compareToIgnoreCase(y.optString("name"));
    if(order.equals("Ad (Z-A)"))sorter=sorter.reversed();
    else if(order.equals("Yıl (yeni)"))sorter=(x,y)->y.optString("year").compareTo(x.optString("year"));
    else if(order.equals("Fiyat (yüksek)"))sorter=(x,y)->Double.compare(amount(y.optString("price")),amount(x.optString("price")));
    final Comparator<JSONObject> baseSorter=sorter;
    Collections.sort(matches,(x,y)->{int pinned=Boolean.compare(y.optBoolean("pinned"),x.optBoolean("pinned"));return pinned!=0?pinned:baseSorter.compare(x,y);});
    content.addView(label("Bulunan kart: "+matches.size(),15,false));
    button("Sıralama: "+order,()->new AlertDialog.Builder(this).setTitle("Sıralama").setItems(new String[]{"Ad (A-Z)","Ad (Z-A)","Yıl (yeni)","Fiyat (yüksek)"},(d,which)->collectionFiltered(query,category,folder,new String[]{"Ad (A-Z)","Ad (Z-A)","Yıl (yeni)","Fiyat (yüksek)"}[which])).show());
    for(JSONObject o:matches){
      final String id=o.optString("id");
      showCardPhoto(o);
      content.addView(label(o.optString("name")+"  ·  "+o.optString("year")+" "+o.optString("brand")+"  ·  $"+o.optString("price"),17,true));
      content.addView(label("📍 Saklama yeri: "+o.optString("storageLocation","Belirtilmedi")+(o.optBoolean("pinned")?"  •  📌 Sabitlenmiş":""),13,false));
      content.addView(label("Klasör: "+o.optString("folder","—")+"  •  Kondisyon: "+o.optString("grade","—")+"\\nNot: "+o.optString("notes",""),13,false));
      button(o.optBoolean("pinned")?"📌 Sabitlemeyi kaldır":"📌 Üste sabitle",()->togglePin(id));
      button("📍 Saklama yerini düzenle",()->editStorage(id));
      button("☆ Favori / kaldır",()->toggle(id));
      button("Kartı düzenle",()->edit(id));
      button("İstek listesine ekle",()->wishAdd(id));
      button("Sil",()->delete(id));
    }
    if(matches.isEmpty())content.addView(label("Henüz eşleşen kart yok.",15,false));
    button("＋ Yeni kart",()->add());button("⌕ Filtrele",()->search());button("← Ana sayfa",()->home());
  }
  double amount(String v){try{return Double.parseDouble(v.trim().replace(",","."));}catch(Exception e){return 0;}}
  void togglePin(String id){for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null&&id.equals(o.optString("id"))){try{o.put("pinned",!o.optBoolean("pinned"));}catch(Exception ignored){}break;}}save();collection("");}
  void editStorage(String id){for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o==null||!id.equals(o.optString("id")))continue;final JSONObject card=o;EditText input=new EditText(this);input.setSingleLine(false);input.setHint("Örn. Raf A / Kutu 3 / Sıra 12");input.setText(o.optString("storageLocation"));new AlertDialog.Builder(this).setTitle("Kartın saklama yeri").setView(input).setNegativeButton("İptal",null).setPositiveButton("Kaydet",(d,w)->{try{card.put("storageLocation",input.getText().toString().trim());save();collection("");}catch(Exception e){message("Kaydedilemedi");}}).show();return;}}
  void toggle(String id){for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null&&id.equals(o.optString("id"))){try{o.put("favorite",!o.optBoolean("favorite"));}catch(Exception ignored){}break;}}save();collection("");}
  void delete(String id){new AlertDialog.Builder(this).setMessage("Kart silinsin mi?").setNegativeButton("İptal",null).setPositiveButton("Sil",(d,w)->{JSONArray next=new JSONArray();for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null&&!id.equals(o.optString("id")))next.put(o);}cards=next;save();collection("");}).show();}
  JSONArray wishData(){try{return new JSONArray(prefs.getString("wish_items","[]"));}catch(Exception e){return new JSONArray();}}
  void wishAdd(String id){for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null&&id.equals(o.optString("id"))){JSONArray a=wishData();for(int j=0;j<a.length();j++)if(id.equals(a.optJSONObject(j).optString("cardId"))){message("Kart zaten istek listesinde");return;}try{JSONObject item=new JSONObject();item.put("cardId",id);item.put("name",o.optString("name"));item.put("set",o.optString("brand"));a.put(item);prefs.edit().putString("wish_items",a.toString()).apply();message("İstek listesine eklendi");}catch(Exception e){message("Eklenemedi");}return;}}}
  void wishlist(){screen("İstek listem");JSONArray a=wishData();for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null)content.addView(label("♡ "+o.optString("name")+" · "+o.optString("set"),16,false));}button("Yeni istek ekle",()->newWish());button("← Ana sayfa",()->home());}
  void newWish(){screen("İstek kartı ekle");EditText name=field("Kart adı"),set=field("Set / seri");button("Kaydet",()->{if(name.getText().toString().trim().isEmpty()){message("Kart adı gerekli");return;}try{JSONArray a=wishData();JSONObject o=new JSONObject();o.put("cardId",UUID.randomUUID().toString());o.put("name",name.getText().toString());o.put("set",set.getText().toString());a.put(o);prefs.edit().putString("wish_items",a.toString()).apply();wishlist();}catch(Exception e){message("Kaydedilemedi");}});}
  JSONArray checklistData(){try{return new JSONArray(prefs.getString("checklists","[]"));}catch(Exception e){return new JSONArray();}}
  void checklists(){screen("Set kontrol listesi");content.addView(label("Kendi set listeni oluştur, kartları tamamlandı olarak işaretle. Resmi set kataloğu henüz bağlı değil.",14,false));JSONArray a=checklistData();int completed=0;for(int i=0;i<a.length();i++)if(a.optJSONObject(i)!=null&&a.optJSONObject(i).optBoolean("owned"))completed++;stat("Tamamlanan kart",completed+" / "+a.length());for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o==null)continue;final String id=o.optString("id");button((o.optBoolean("owned")?"☑ ":"☐ ")+o.optString("set")+" · "+o.optString("card"),()->toggleChecklist(id));}button("🖨 Yazdırılabilir listeyi paylaş",()->shareChecklist());button("Set kartı ekle",()->newChecklist());button("← Ana sayfa",()->home());}
  void shareChecklist(){JSONArray a=checklistData();if(a.length()==0){message("Önce set listesine kart ekle");return;}StringBuilder out=new StringBuilder("CARDVISION AI — SET KONTROL LİSTESİ\n\n");int owned=0;for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o==null)continue;boolean done=o.optBoolean("owned");if(done)owned++;out.append(done?"[X] ":"[ ] ").append(o.optString("set")).append(" — ").append(o.optString("card")).append("\n");}out.append("\nTamamlanan: ").append(owned).append(" / ").append(a.length());Intent share=new Intent(Intent.ACTION_SEND);share.setType("text/plain");share.putExtra(Intent.EXTRA_TEXT,out.toString());startActivity(Intent.createChooser(share,"Listeyi yazdır / paylaş"));}
  void newChecklist(){screen("Set kartı ekle");EditText set=field("Set adı"),card=field("Kart adı / numarası");button("Listeye ekle",()->{if(card.getText().toString().trim().isEmpty()){message("Kart adı gerekli");return;}try{JSONArray a=checklistData();JSONObject o=new JSONObject();o.put("id",UUID.randomUUID().toString());o.put("set",set.getText().toString());o.put("card",card.getText().toString());o.put("owned",false);a.put(o);prefs.edit().putString("checklists",a.toString()).apply();checklists();}catch(Exception e){message("Kaydedilemedi");}});}
  void toggleChecklist(String id){JSONArray a=checklistData();for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null&&id.equals(o.optString("id"))){try{o.put("owned",!o.optBoolean("owned"));}catch(Exception ignored){}break;}}prefs.edit().putString("checklists",a.toString()).apply();checklists();}
  void folders(){screen("Klasörler");java.util.TreeSet<String> names=new java.util.TreeSet<>();for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null&&!o.optString("folder").trim().isEmpty())names.add(o.optString("folder"));}if(names.isEmpty())content.addView(label("Henüz klasör yok. Kart eklerken klasör adı yazabilirsin.",15,false));for(String name:names)button("▤ "+name,()->collectionByFolder(name));button("← Ana sayfa",()->home());}
  void collectionByFolder(String folder){screen("Klasör: "+folder);for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null&&folder.equals(o.optString("folder"))){final String id=o.optString("id");content.addView(label(o.optString("name"),17,true));button("Düzenle",()->edit(id));}}button("← Klasörler",()->folders());}
  void edit(String id){JSONObject card=null;for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null&&id.equals(o.optString("id"))){card=o;break;}}if(card==null){message("Kart bulunamadı");return;}final JSONObject target=card;screen("Kartı düzenle");String[] keys={"name","sport","brand","year","number","variant","price","cost","folder","grade","notes"};String[] hints={"Kart adı","Spor / TCG","Marka / set","Yıl","Kart numarası","Varyant","Tahmini değer USD","Alış fiyatı USD","Klasör","Kondisyon","Notlar"};EditText[] fields=new EditText[keys.length];for(int i=0;i<keys.length;i++){fields[i]=field(hints[i]);fields[i].setText(card.optString(keys[i]));}button("Değişiklikleri kaydet",()->{try{for(int i=0;i<keys.length;i++)target.put(keys[i],fields[i].getText().toString());save();collection("");}catch(Exception e){message("Kaydetme hatası");}});}
  void importBackup(){screen("JSON yedeği içe aktar");content.addView(label("Yedek JSON metnini yapıştır. Mevcut kartlar korunur; aynı ID tekrar eklenmez.",15,false));EditText data=field("JSON koleksiyon dizisi");data.setMinLines(5);button("Kartları içe aktar",()->{try{JSONArray incoming=new JSONArray(data.getText().toString());HashSet<String> ids=new HashSet<>();for(int i=0;i<cards.length();i++)ids.add(cards.optJSONObject(i).optString("id"));int added=0;for(int i=0;i<incoming.length();i++){JSONObject o=incoming.optJSONObject(i);if(o==null||o.optString("name").trim().isEmpty())continue;String id=o.optString("id");if(id.isEmpty()){id=UUID.randomUUID().toString();o.put("id",id);}if(ids.add(id)){cards.put(o);added++;}}save();message(added+" kart içe aktarıldı.");}catch(Exception e){message("Geçersiz JSON. İşlem iptal edildi.");}});button("← Ana sayfa",()->home());}
  void favorites(){screen("Favoriler");for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null&&o.optBoolean("favorite"))content.addView(label("★ "+o.optString("name"),17,false));}button("← Ana sayfa",()->home());}
  void collectionStats(){screen("Koleksiyon istatistikleri");int count=0,favorites=0,graded=0,pinned=0;double total=0,cost=0;HashMap<String,Integer> groups=new HashMap<>();for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o==null)continue;count++;if(o.optBoolean("favorite"))favorites++;if(o.optBoolean("pinned"))pinned++;if(!o.optString("grade").trim().isEmpty())graded++;total+=amount(o.optString("price"));cost+=amount(o.optString("cost"));String category=o.optString("sport","Diğer").trim();if(category.isEmpty())category="Diğer";groups.put(category,groups.containsKey(category)?groups.get(category)+1:1);}stat("Toplam kart",String.valueOf(count));stat("Favoriler",String.valueOf(favorites));stat("Sabitlenen",String.valueOf(pinned));stat("Derecelendirme bilgisi olan",String.valueOf(graded));content.addView(label(String.format(Locale.US,"Manuel toplam değer: $%.2f | Alış: $%.2f | Fark: $%.2f",total,cost,total-cost),16,true));content.addView(label("Kategori dağılımı",18,true));ArrayList<String> keys=new ArrayList<>(groups.keySet());Collections.sort(keys);for(String key:keys)content.addView(label(key+": "+groups.get(key),15,false));content.addView(label("Değerler kullanıcının girdiği tutarlardır; canlı piyasa fiyatı değildir.",13,false));button("← Ana sayfa",()->home());}
  void portfolio(){screen("Portföy");double sum=0,cost=0;for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null)try{sum+=Double.parseDouble(o.optString("price","0"));}catch(Exception ignored){}}for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null)try{cost+=Double.parseDouble(o.optString("cost","0"));}catch(Exception ignored){}}content.addView(label(String.format(Locale.US,"Toplam manuel değer: $%.2f",sum),23,true));content.addView(label(String.format(Locale.US,"Toplam alış: $%.2f  •  Fark: $%.2f",cost,sum-cost),16,false));content.addView(label("Bu değer canlı piyasa verisi değildir.",14,false));button("← Ana sayfa",()->home());}
  void export(){Intent intent=new Intent(Intent.ACTION_SEND);intent.setType("application/json");intent.putExtra(Intent.EXTRA_TEXT,cards.toString());startActivity(Intent.createChooser(intent,"Koleksiyonu paylaş"));}
  String csv(String v){return "\"" + (v==null?"":v).replace("\"","\"\"") + "\"";}
  void exportCsv(){String[] keys={"name","sport","brand","year","number","variant","price","cost","folder","grade","notes"};StringBuilder b=new StringBuilder();for(int k=0;k<keys.length;k++){if(k>0)b.append(',');b.append(keys[k]);}b.append('\n');for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o==null)continue;for(int k=0;k<keys.length;k++){if(k>0)b.append(',');b.append(csv(o.optString(keys[k])));}b.append('\n');}Intent send=new Intent(Intent.ACTION_SEND);send.setType("text/csv");send.putExtra(Intent.EXTRA_TEXT,b.toString());startActivity(Intent.createChooser(send,"CSV koleksiyonunu paylaş"));}
  JSONArray listingData(){try{return new JSONArray(prefs.getString("listing_drafts","[]"));}catch(Exception e){return new JSONArray();}}
  void exportListingCsv(){JSONArray a=listingData();if(a.length()==0){message("Dışa aktarılacak ilan yok");return;}StringBuilder b=new StringBuilder("Başlık,Fiyat USD,Kondisyon,Açıklama\n");for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o==null)continue;b.append(csv(o.optString("title"))).append(",").append(csv(o.optString("price"))).append(",").append(csv(o.optString("condition"))).append(",").append(csv(o.optString("description"))).append("\n");}Intent send=new Intent(Intent.ACTION_SEND);send.setType("text/csv");send.putExtra(Intent.EXTRA_SUBJECT,"CardVision ilan taslakları CSV");send.putExtra(Intent.EXTRA_TEXT,b.toString());startActivity(Intent.createChooser(send,"CSV paylaş"));}
  void listings(){screen("Satış ilanı taslakları");content.addView(label("Bu ilanlar yalnızca cihazda saklanır; gerçek pazaryerinde yayımlanmaz.",14,false));JSONArray a=listingData();for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null)content.addView(label(o.optString("title")+" · $"+o.optString("price")+"\n"+o.optString("condition")+" · "+o.optString("description"),15,true));}button("＋ İlan taslağı oluştur",()->newListing());button("⇧ İlan taslaklarını CSV paylaş",()->exportListingCsv());button("← Piyasa",()->market());}
  void newListing(){screen("Yeni ilan taslağı");EditText title=field("Kart başlığı"),price=field("Satış fiyatı USD"),condition=field("Kondisyon"),description=field("Açıklama");button("Taslağı kaydet",()->{if(title.getText().toString().trim().isEmpty()){message("Başlık gerekli");return;}try{JSONArray a=listingData();JSONObject o=new JSONObject();o.put("id",UUID.randomUUID().toString());o.put("title",title.getText().toString());o.put("price",price.getText().toString());o.put("condition",condition.getText().toString());o.put("description",description.getText().toString());a.put(o);prefs.edit().putString("listing_drafts",a.toString()).apply();listings();}catch(Exception e){message("Taslak kaydedilemedi");}});}
  void market(){screen("Kart piyasası");content.addView(label("Gerçek satış fiyatları ve alım-satım entegrasyonları henüz bağlı değil. Bu bölümde sahte ilan veya fiyat gösterilmez.",16,false));button("Yerel portföy değerim",()->portfolio());button("Satış ilanı taslaklarım",()->listings());button("← Ana sayfa",()->home());}
  void profile(){screen("Profil");stat("Kaydedilmiş kart",String.valueOf(cards.length()));button("Favorilerim",()->favorites());button("Koleksiyon yedeğini paylaş",()->export());button("JSON yedeği içe aktar",()->importBackup());button(dark?"Açık temaya geç":"Koyu temaya geç",()->{dark=!dark;prefs.edit().putBoolean("dark",dark).apply();profile();});button("Entegrasyonlar",()->status());}
  void status(){screen("Bağlantılar");content.addView(label("Yerel koleksiyon: Çalışır\nAI kart tanıma: Henüz bağlı değil\nCanlı fiyat verisi: Henüz bağlı değil\nBulut hesapları: Henüz bağlı değil\nPazaryeri ve eBay: Henüz bağlı değil\nPremium ödeme: Henüz bağlı değil",16,false));button("← Ana sayfa",()->home());}
}