package com.cardvision.ai;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.util.*;

public class MainActivity extends Activity {
  LinearLayout root, content;
  JSONArray cards;
  int blue=Color.rgb(38,99,235), ink=Color.rgb(22,34,57);
  android.content.SharedPreferences prefs;
  @Override public void onCreate(Bundle b){super.onCreate(b);prefs=getSharedPreferences("cards",MODE_PRIVATE);load();home();}
  void load(){try{cards=new JSONArray(prefs.getString("items","[]"));}catch(Exception e){cards=new JSONArray();}}
  void save(){prefs.edit().putString("items",cards.toString()).apply();}
  TextView label(String s,int size,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(ink);t.setPadding(8,12,8,12);if(bold)t.setTypeface(null,1);return t;}
  void screen(String title){root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(Color.rgb(246,248,253));setContentView(root);TextView h=label(title,26,true);h.setPadding(24,32,24,20);root.addView(h);ScrollView scroll=new ScrollView(this);content=new LinearLayout(this);content.setPadding(20,8,20,20);content.setOrientation(1);scroll.addView(content);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));}
  void button(String title,Runnable action){Button b=new Button(this);b.setText(title);b.setTextColor(blue);content.addView(b,new LinearLayout.LayoutParams(-1,-2));b.setOnClickListener(v->action.run());}
  EditText field(String hint){EditText e=new EditText(this);e.setHint(hint);content.addView(e);return e;}
  void message(String text){new AlertDialog.Builder(this).setMessage(text).setPositiveButton("Tamam",null).show();}
  void home(){screen("CardVision AI");content.addView(label("Kartlarını keşfet, koleksiyonunu yönet",16,false));button("＋ Kart ekle",()->add());button("▦ Koleksiyonum ("+cards.length()+")",()->collection(""));button("⌕ Kart ara",()->search());button("☆ Favoriler",()->favorites());button("◷ Portföy değeri",()->portfolio());button("⇧ Koleksiyonu paylaş",()->export());button("⚙ Entegrasyon durumu",()->status());}
  void add(){screen("Yeni kart");EditText name=field("Kart / oyuncu adı"),sport=field("Spor veya TCG"),brand=field("Marka / set"),year=field("Yıl"),number=field("Kart numarası"),variant=field("Varyant / parallel"),price=field("Tahmini değer (USD)");button("Kartı kaydet",()->{String n=name.getText().toString().trim();if(n.isEmpty()){message("Kart adı gerekli");return;}try{JSONObject o=new JSONObject();o.put("id",UUID.randomUUID().toString());o.put("name",n);o.put("sport",sport.getText());o.put("brand",brand.getText());o.put("year",year.getText());o.put("number",number.getText());o.put("variant",variant.getText());o.put("price",price.getText());o.put("favorite",false);cards.put(o);save();home();}catch(Exception e){message("Kaydedilemedi");}});button("← Geri",()->home());}
  void search(){screen("Kart ara");EditText q=field("Oyuncu, marka, yıl, seri");button("Ara",()->collection(q.getText().toString()));button("← Geri",()->home());}
  void collection(String query){screen("Koleksiyonum");int count=0;for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o==null||!o.toString().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))continue;count++;final String id=o.optString("id");content.addView(label(o.optString("name")+"  ·  "+o.optString("year")+" "+o.optString("brand")+"  ·  $"+o.optString("price"),17,true));button("☆ Favori / kaldır",()->toggle(id));button("Sil",()->delete(id));}if(count==0)content.addView(label("Henüz eşleşen kart yok.",15,false));button("＋ Yeni kart",()->add());button("← Ana sayfa",()->home());}
  void toggle(String id){for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null&&id.equals(o.optString("id"))){try{o.put("favorite",!o.optBoolean("favorite"));}catch(Exception ignored){}break;}}save();collection("");}
  void delete(String id){new AlertDialog.Builder(this).setMessage("Kart silinsin mi?").setNegativeButton("İptal",null).setPositiveButton("Sil",(d,w)->{JSONArray next=new JSONArray();for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null&&!id.equals(o.optString("id")))next.put(o);}cards=next;save();collection("");}).show();}
  void favorites(){screen("Favoriler");for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null&&o.optBoolean("favorite"))content.addView(label("★ "+o.optString("name"),17,false));}button("← Ana sayfa",()->home());}
  void portfolio(){screen("Portföy");double sum=0;for(int i=0;i<cards.length();i++){JSONObject o=cards.optJSONObject(i);if(o!=null)try{sum+=Double.parseDouble(o.optString("price","0"));}catch(Exception ignored){}}content.addView(label(String.format(Locale.US,"Toplam manuel değer: $%.2f",sum),23,true));content.addView(label("Bu değer canlı piyasa verisi değildir.",14,false));button("← Ana sayfa",()->home());}
  void export(){Intent intent=new Intent(Intent.ACTION_SEND);intent.setType("application/json");intent.putExtra(Intent.EXTRA_TEXT,cards.toString());startActivity(Intent.createChooser(intent,"Koleksiyonu paylaş"));}
  void status(){screen("Bağlantılar");content.addView(label("Yerel koleksiyon: Çalışır\nAI kart tanıma: Henüz bağlı değil\nCanlı fiyat verisi: Henüz bağlı değil\nBulut hesapları: Henüz bağlı değil\nPazaryeri ve eBay: Henüz bağlı değil\nPremium ödeme: Henüz bağlı değil",16,false));button("← Ana sayfa",()->home());}
}