package tj.shohin.chinese;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.text.*;
import android.speech.tts.TextToSpeech;
import java.util.*;

public class MainActivity extends Activity {
    final int BG=Color.rgb(5,22,17), PANEL=Color.rgb(12,43,32), PANEL2=Color.rgb(17,61,44),
        GREEN=Color.rgb(8,165,108), GREEN2=Color.rgb(5,126,81), GOLD=Color.rgb(205,169,76),
        LIME=Color.rgb(186,240,91), WHITE=Color.rgb(246,248,242), MUTED=Color.rgb(164,187,173);
    LinearLayout root, body, nav;
    TextToSpeech tts;
    boolean ttsReady=false;
    Set<String> completed=new HashSet<>(), favorites=new HashSet<>();
    int quizScore=0, quizDone=0;
    final String[][] lessons={
      {"Салом ва шиносоӣ","你好","nǐ hǎo","Салом","谢谢","xièxie","Ташаккур","再见","zàijiàn","То дидор","我","wǒ","Ман","你","nǐ","Ту / Шумо","我是塔吉克斯坦人","wǒ shì Tǎjíkèsītǎn rén","Ман тоҷикистониям"},
      {"Рақамҳо","一","yī","Як","二","èr","Ду","三","sān","Се","四","sì","Чор","五","wǔ","Панҷ","六","liù","Шаш","七","qī","Ҳафт","八","bā","Ҳашт","九","jiǔ","Нӯҳ","十","shí","Даҳ"},
      {"Оила","爸爸","bàba","Падар","妈妈","māma","Модар","哥哥","gēge","Бародари калон","姐姐","jiějie","Хоҳари калон","弟弟","dìdi","Бародари хурдӣ","妹妹","mèimei","Хоҳари хурдӣ","家","jiā","Хона / оила"},
      {"Хӯрок ва нӯшокӣ","水","shuǐ","Об","茶","chá","Чой","米饭","mǐfàn","Биринҷ / хӯрок","面条","miàntiáo","Угро","苹果","píngguǒ","Себ","好吃","hǎochī","Бомаза"},
      {"Савдо ва харид","多少钱？","duōshao qián?","Чанд пул?","太贵了","tài guì le","Хеле қимат","便宜一点","piányi yìdiǎn","Каме арзонтар","买","mǎi","Харидан","这个","zhège","Ин","不要","bú yào","Даркор нест"},
      {"Ҷумлаҳои муҳим","我不懂","wǒ bù dǒng","Ман намефаҳмам","请再说一次","qǐng zài shuō yí cì","Лутфан такрор кунед","厕所在哪里？","cèsuǒ zài nǎlǐ?","Ҳоҷатхона дар куҷост?","帮助我","bāngzhù wǒ","Ба ман ёрӣ диҳед","请","qǐng","Лутфан","对不起","duìbuqǐ","Бубахшед"}
    };
    @Override public void onCreate(Bundle b){
      super.onCreate(b);
      getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(BG);
      android.content.SharedPreferences p=getPreferences(0);
      completed=new HashSet<>(p.getStringSet("completed",new HashSet<>()));
      favorites=new HashSet<>(p.getStringSet("favorites",new HashSet<>()));
      tts=new TextToSpeech(this,status->{if(status==TextToSpeech.SUCCESS){int r=tts.setLanguage(Locale.SIMPLIFIED_CHINESE);ttsReady=r!=TextToSpeech.LANG_MISSING_DATA&&r!=TextToSpeech.LANG_NOT_SUPPORTED;}});
      showHome();
    }
    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density);}
    GradientDrawable shape(int color,int radius,int stroke){
      GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));
      if(stroke>0)d.setStroke(dp(1),stroke);return d;
    }
    TextView label(String s,int size,int color,boolean bold){
      TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);
      t.setGravity(Gravity.CENTER_VERTICAL);t.setPadding(0,dp(3),0,dp(3));
      if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;
    }
    LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
    LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    void gap(LinearLayout p,int h){View v=new View(this);p.addView(v,new LinearLayout.LayoutParams(1,dp(h)));}
    void card(LinearLayout parent,View child){
      LinearLayout c=column();c.setPadding(dp(16),dp(14),dp(16),dp(14));c.setBackground(shape(PANEL,18,Color.rgb(31,83,60)));
      LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(11);parent.addView(c,lp);c.addView(child);
    }
    Button action(String s,boolean primary){
      Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextSize(14);
      b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setTextColor(primary?BG:WHITE);
      b.setBackground(shape(primary?LIME:PANEL2,16,primary?GOLD:GREEN2));
      b.setPadding(dp(12),dp(8),dp(12),dp(8));return b;
    }
    void base(String title,String subtitle,String active){
      root=column();root.setBackgroundColor(BG);setContentView(root);
      LinearLayout head=column();head.setPadding(dp(20),dp(13),dp(20),dp(13));head.setBackgroundColor(PANEL);
      TextView brand=label("♛  SHOHIN TAJIKISTAN",12,GOLD,true);head.addView(brand);
      TextView h=label(title,23,WHITE,true);head.addView(h);
      head.addView(label(subtitle,12,MUTED,false));root.addView(head);
      ScrollView sc=new ScrollView(this);sc.setFillViewport(false);body=column();
      body.setPadding(dp(17),dp(16),dp(17),dp(22));sc.addView(body);
      root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
      nav=row();nav.setPadding(dp(5),dp(6),dp(5),dp(6));nav.setBackgroundColor(PANEL);
      addNav("⌂","Асосӣ","home",active);addNav("▤","Дарсҳо","lessons",active);
      addNav("⌕","Луғат","dict",active);addNav("◉","Пешрафт","progress",active);
      root.addView(nav);
    }
    void addNav(String icon,String title,String id,String active){
      LinearLayout item=column();item.setGravity(Gravity.CENTER);item.setPadding(0,dp(3),0,dp(3));
      TextView a=label(icon,19,id.equals(active)?LIME:MUTED,true);a.setGravity(Gravity.CENTER);item.addView(a);
      TextView b=label(title,10,id.equals(active)?LIME:MUTED,true);b.setGravity(Gravity.CENTER);item.addView(b);
      item.setOnClickListener(v->{if(id.equals("home"))showHome();else if(id.equals("lessons"))showLessons();else if(id.equals("dict"))showDictionary();else showProgress();});
      nav.addView(item,new LinearLayout.LayoutParams(0,-2,1));
    }
    void showHome(){
      base("Омӯзиши забони чинӣ","Аз сифр оғоз кунед • Тоҷикӣ → 中文","home");
      LinearLayout hero=column();hero.setPadding(dp(19),dp(18),dp(19),dp(18));
      hero.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(12,104,66),Color.rgb(5,37,27)}));
      hero.setCornerRadius(dp(22));hero.setStroke(dp(1),GOLD);
      hero.addView(label("你好",35,LIME,true));hero.addView(label("Nǐ hǎo  •  Салом!",18,WHITE,true));
      hero.addView(label("Қадами аввал ба забони чинӣ",13,MUTED,false));
      gap(hero,10);
      TextView progress=label("Пешрафти шумо: "+completed.size()+" / "+lessons.length+" дарс",13,WHITE,true);hero.addView(progress);
      ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
      bar.setMax(lessons.length);bar.setProgress(completed.size());bar.setProgressTintList(ColorStateList.valueOf(LIME));
      bar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(42,78,58)));
      hero.addView(bar,new LinearLayout.LayoutParams(-1,dp(8)));body.addView(hero);
      gap(body,15);body.addView(label("Идома диҳед",19,WHITE,true));
      LinearLayout quick=row();Button go=action("▶  Дарсҳоро оғоз кардан",true);
      go.setOnClickListener(v->showLessons());quick.addView(go,new LinearLayout.LayoutParams(-1,dp(52)));body.addView(quick);
      gap(body,17);body.addView(label("Қисмҳои омӯзиш",18,WHITE,true));
      addFeature("▤","Дарсҳо","6 мавзӯъ • калимаҳо ва ибораҳо",()->showLessons());
      addFeature("⌕","Луғати чинӣ","Ҷустуҷӯи иероглиф ва тарҷума",()->showDictionary());
      addFeature("✓","Санҷиш","Дониши худро бисанҷед",()->showQuiz());
      addFeature("★","Дастовардҳо","Натиҷа ва пешрафти омӯзиш",()->showProgress());
      gap(body,6);body.addView(label("Маслиҳат: ҳар рӯз 10 дақиқа омӯзед ва калимаҳоро бо овоз такрор кунед.",13,GOLD,false));
    }
    void addFeature(String icon,String title,String desc,Runnable click){
      LinearLayout r=row();TextView ic=label(icon,24,GOLD,true);ic.setGravity(Gravity.CENTER);
      r.addView(ic,new LinearLayout.LayoutParams(dp(42),dp(44)));
      LinearLayout c=column();c.addView(label(title,15,WHITE,true));c.addView(label(desc,12,MUTED,false));
      r.addView(c,new LinearLayout.LayoutParams(0,-2,1));r.addView(label("›",25,GOLD,true));
      card(body,r);r.setOnClickListener(v->click.run());
    }
    void showLessons(){
      base("Дарсҳои омӯзишӣ","Аз осон ба душвор • 6 мавзӯи асосӣ","lessons");
      body.addView(label("Сатҳи ибтидоӣ  •  A1",14,GOLD,true));
      for(int i=0;i<lessons.length;i++){
        final int n=i;LinearLayout r=row();TextView num=label(String.format(Locale.US,"%02d",i+1),22,GOLD,true);
        num.setGravity(Gravity.CENTER);r.addView(num,new LinearLayout.LayoutParams(dp(48),dp(48)));
        LinearLayout c=column();c.addView(label(lessons[i][0],16,WHITE,true));
        c.addView(label((completed.contains(""+i)?"Анҷом ёфтааст":"Иероглифҳо • талаффуз • тарҷума"),12,MUTED,false));
        r.addView(c,new LinearLayout.LayoutParams(0,-2,1));r.addView(label(completed.contains(""+i)?"✓":"›",22,completed.contains(""+i)?LIME:GOLD,true));
        card(body,r);r.setOnClickListener(v->showLesson(n));
      }
    }
    void speak(String han){
      if(ttsReady)tts.speak(han,TextToSpeech.QUEUE_FLUSH,null,"shohin_word");
      else Toast.makeText(this,"Овози чинӣ насб нашудааст. Pinyin-ро хонед.",Toast.LENGTH_LONG).show();
    }
    void showLesson(int index){
      String[] l=lessons[index];base("Дарси "+(index+1),l[0],"lessons");
      body.addView(label("Гӯш кунед • хонед • такрор кунед",13,MUTED,false));
      for(int j=1;j<l.length;j+=3){
        final String han=l[j],pin=l[j+1],taj=l[j+2],key=han;
        LinearLayout content=column();content.addView(label(han,34,LIME,true));
        content.addView(label(pin,17,WHITE,true));content.addView(label(taj,14,MUTED,false));
        LinearLayout buttons=row();Button play=action("▶  Гӯш кардан",false);
        play.setOnClickListener(v->speak(han));buttons.addView(play,new LinearLayout.LayoutParams(0,dp(44),1));
        Button fav=action(favorites.contains(key)?"★":"☆",false);
        fav.setOnClickListener(v->{toggleFavorite(key);showLesson(index);});
        LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(dp(55),dp(44));fp.leftMargin=dp(8);buttons.addView(fav,fp);
        content.addView(buttons);card(body,content);
      }
      Button done=action(completed.contains(""+index)?"✓  Дарс анҷом ёфтааст":"Дарсро анҷом додам  ✓",true);
      done.setOnClickListener(v->{completed.add(""+index);save();Toast.makeText(this,"Пешрафт нигоҳ дошта шуд.",Toast.LENGTH_SHORT).show();showLessons();});
      body.addView(done,new LinearLayout.LayoutParams(-1,dp(52)));
    }
    void toggleFavorite(String word){if(favorites.contains(word))favorites.remove(word);else favorites.add(word);save();}
    void save(){
      getPreferences(0).edit().putStringSet("completed",new HashSet<>(completed))
        .putStringSet("favorites",new HashSet<>(favorites)).apply();
    }
    ArrayList<String[]> allWords(){
      ArrayList<String[]> list=new ArrayList<>();
      for(String[] l:lessons)for(int j=1;j<l.length;j+=3)list.add(new String[]{l[j],l[j+1],l[j+2]});
      return list;
    }
    void showDictionary(){dictionaryScreen(false);}
    void dictionaryScreen(boolean onlyFav){
      base(onlyFav?"Калимаҳои интихобшуда":"Луғати чинӣ",onlyFav?"Калимаҳои нигоҳдошта":"Ҷустуҷӯи чинӣ, Pinyin ё тарҷума","dict");
      EditText search=new EditText(this);search.setSingleLine(true);search.setTextColor(WHITE);search.setHintTextColor(MUTED);
      search.setHint("⌕  Калима ё тарҷумаро ҷустуҷӯ кунед");search.setTextSize(14);search.setPadding(dp(14),dp(10),dp(14),dp(10));
      search.setBackground(shape(PANEL2,14,GOLD));body.addView(search);
      LinearLayout results=column();body.addView(results);ArrayList<String[]> words=allWords();
      Runnable render=()->{
        results.removeAllViews();String q=search.getText().toString().toLowerCase(Locale.ROOT);
        int count=0;
        for(String[] w:words){
          if(onlyFav&&!favorites.contains(w[0]))continue;
          if(!q.isEmpty()&&!(w[0].toLowerCase(Locale.ROOT).contains(q)||w[1].toLowerCase(Locale.ROOT).contains(q)||w[2].toLowerCase(Locale.ROOT).contains(q)))continue;
          count++;LinearLayout c=column();c.addView(label(w[0],27,LIME,true));c.addView(label(w[1],14,WHITE,true));c.addView(label(w[2],13,MUTED,false));
          LinearLayout acts=row();Button play=action("▶  Овоз",false);play.setOnClickListener(v->speak(w[0]));acts.addView(play,new LinearLayout.LayoutParams(0,dp(40),1));
          Button fav=action(favorites.contains(w[0])?"★":"☆",false);fav.setOnClickListener(v->{toggleFavorite(w[0]);dictionaryScreen(onlyFav);});LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(dp(55),dp(40));fp.leftMargin=dp(8);acts.addView(fav,fp);c.addView(acts);card(results,c);
        }
        if(count==0)results.addView(label(onlyFav?"Ҳоло калимаи интихобшуда нест":"Калима ёфт нашуд.",14,MUTED,false));
      };
      search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){render.run();}public void afterTextChanged(Editable e){}});
      render.run();
      Button favs=action(onlyFav?"← Ба луғат":"★  Калимаҳои интихобшуда",false);
      favs.setOnClickListener(v->{if(onlyFav)showDictionary();else dictionaryScreen(true);});body.addView(favs);
    }
    void showQuiz(){
      base("Санҷиши дониш","Интихоб кунед ва ҷавобро санҷед","home");
      String[][] qs={{"你好","Салом","Ташаккур","Хайр","0"},{"谢谢","Об","Ташаккур","Лутфан","1"},{"水","Чой","Об","Хӯрок","1"},{"爸爸","Модар","Падар","Бародари калон","1"},{"多少钱？","Чанд пул?","Ин чист?","Дар куҷо?","0"},{"再见","Салом","Бубахшед","То дидор","2"}};
      final int[] pos={0},score={0},picked={-1};TextView count=label("",13,MUTED,true),word=label("",38,LIME,true),feedback=label("",14,GOLD,true);
      body.addView(count);card(body,word);body.addView(label("Маънои ин калима чист?",14,WHITE,true));
      LinearLayout choices=column();body.addView(choices);body.addView(feedback);
      Runnable[] draw=new Runnable[1];
      draw[0]=()->{
        choices.removeAllViews();feedback.setText("");
        if(pos[0]>=qs.length){word.setText("Натиҷа: "+score[0]+" / "+qs.length);count.setText("Санҷиш ба анҷом расид");Button again=action("Аз нав гузаштан",true);again.setOnClickListener(v->{pos[0]=0;score[0]=0;draw[0].run();});choices.addView(again);return;}
        count.setText("Саволи "+(pos[0]+1)+" аз "+qs.length);word.setText(qs[pos[0]][0]);
        for(int k=1;k<=3;k++){final int option=k;Button b=action(qs[pos[0]][k],false);b.setOnClickListener(v->{if(picked[0]!=-1)return;picked[0]=option;if(option-1==Integer.parseInt(qs[pos[0]][4])){score[0]++;feedback.setText("Дуруст! Офарин.");}else feedback.setText("Ин ҷавоб дуруст нест. Ҷавоби дуруст: "+qs[pos[0]][Integer.parseInt(qs[pos[0]][4])+1]);Button next=action("Саволи навбатӣ  →",true);choices.addView(next);next.setOnClickListener(x->{pos[0]++;picked[0]=-1;draw[0].run();});});choices.addView(b,new LinearLayout.LayoutParams(-1,dp(48)));View spacer=new View(this);choices.addView(spacer,new LinearLayout.LayoutParams(1,dp(7)));}
      };
      draw[0].run();
    }
    void showProgress(){
      base("Пешрафти шумо","Ҳамаи натиҷаҳо дар телефон нигоҳ дошта мешаванд","progress");
      LinearLayout hero=column();hero.setGravity(Gravity.CENTER);hero.setPadding(dp(18),dp(22),dp(18),dp(22));hero.setBackground(shape(PANEL2,22,GOLD));
      hero.addView(label(completed.size()*100/lessons.length+"%",36,LIME,true));
      hero.addView(label("Дараҷаи омӯзиш: A1",16,WHITE,true));
      hero.addView(label(completed.size()+" аз "+lessons.length+" дарс анҷом ёфт",13,MUTED,false));body.addView(hero);
      gap(body,15);
      addStat("▤","Дарсҳои анҷомёфта",completed.size()+" / "+lessons.length);
      addStat("★","Калимаҳои интихобшуда",favorites.size()+"");
      addStat("✓","Мавзӯъҳои дастрас",lessons.length+"");
      gap(body,8);body.addView(label("Дастовардҳо",18,WHITE,true));
      addAchievement("♛","Қадами аввал","Як дарсро анҷом диҳед",completed.size()>=1);
      addAchievement("★","Омӯзандаи фаъол","Се дарсро анҷом диҳед",completed.size()>=3);
      addAchievement("🏆","Устоди ибтидоӣ","Ҳамаи шаш дарсро анҷом диҳед",completed.size()==lessons.length);
      Button quiz=action("Санҷидани дониш  ✓",true);quiz.setOnClickListener(v->showQuiz());body.addView(quiz,new LinearLayout.LayoutParams(-1,dp(50)));
    }
    void addStat(String icon,String title,String value){
      LinearLayout r=row();r.addView(label(icon,23,GOLD,true),new LinearLayout.LayoutParams(dp(42),dp(40)));
      r.addView(label(title,14,WHITE,true),new LinearLayout.LayoutParams(0,-2,1));
      r.addView(label(value,16,LIME,true));card(body,r);
    }
    void addAchievement(String icon,String title,String desc,boolean earned){
      LinearLayout r=row();r.addView(label(icon,25,earned?GOLD:MUTED,true),new LinearLayout.LayoutParams(dp(45),dp(42)));
      LinearLayout c=column();c.addView(label(title,14,WHITE,true));c.addView(label(desc,12,MUTED,false));
      r.addView(c,new LinearLayout.LayoutParams(0,-2,1));r.addView(label(earned?"✓":"🔒",17,earned?LIME:MUTED,true));card(body,r);
    }
    @Override public void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}