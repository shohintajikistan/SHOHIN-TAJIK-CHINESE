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
import java.io.*;

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
    void speak(String text){
      if(tts!=null&&ttsReady){
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"shohin-tts-"+System.nanoTime());
      }else{
        Toast.makeText(this,"Барои шунидани чинӣ овози Chinese TTS-ро дар танзимоти телефон насб кунед.",Toast.LENGTH_LONG).show();
      }
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
      GradientDrawable heroBg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(12,104,66),Color.rgb(5,37,27)});
      heroBg.setCornerRadius(dp(22));heroBg.setStroke(dp(1),GOLD);hero.setBackground(heroBg);
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
    ArrayList<String[]> hskCharacters(){
      ArrayList<String[]> out=new ArrayList<>();
      try(BufferedReader br=new BufferedReader(new InputStreamReader(getAssets().open("hanzi_recognition.tsv"),"UTF-8"))){
        String line;boolean first=true;
        while((line=br.readLine())!=null){
          if(first){first=false;continue;}
          String[] p=line.split("\\t");
          if(p.length>=2)out.add(new String[]{p[0],p[1]});
        }
      }catch(Exception e){Toast.makeText(this,"Файли HSK ёфт нашуд: "+e.getMessage(),Toast.LENGTH_LONG).show();}
      return out;
    }
    ArrayList<String[]> charsForLevel(int level){
      ArrayList<String[]> all=hskCharacters(),out=new ArrayList<>();
      String wanted=level<6?"HSK"+(level+1):"HSK7-9";
      for(String[] item:all)if(item[0].equals(wanted))out.add(item);
      return out;
    }
    String levelName(int level){return level<6?"HSK "+(level+1):"HSK 7–9";}
    void showLessons(){
      base("Барномаи HSK 3.0","3 088 иероглиф • рӯйхати расмии сатҳҳо","lessons");
      ArrayList<String[]> all=hskCharacters();
      body.addView(label("Омӯзиш аз рӯйи рӯйхати HSK 3.0",17,WHITE,true));
      body.addView(label("Иероглифҳо дар гурӯҳҳои 20-тоӣ. Барои ҳар гурӯҳ машқи шинохти шакл ва озмоиш мавҷуд аст.",13,MUTED,false));
      int[] counts={246,125,284,441,431,413,1148};
      for(int i=0;i<7;i++){
        final int level=i;int count=counts[i];int groups=(count+19)/20;
        LinearLayout r=row();TextView n=label(String.format(Locale.US,"%02d",i+1),22,GOLD,true);n.setGravity(Gravity.CENTER);
        r.addView(n,new LinearLayout.LayoutParams(dp(48),dp(48)));
        LinearLayout c=column();c.addView(label(levelName(i),16,WHITE,true));
        c.addView(label(count+" иероглиф • "+groups+" гурӯҳи омӯзишӣ",12,MUTED,false));
        r.addView(c,new LinearLayout.LayoutParams(0,-2,1));r.addView(label("›",24,GOLD,true));
        card(body,r);r.setOnClickListener(v->showHskLevel(level));
      }
      gap(body,8);
      body.addView(label("Маълумот: сатҳҳои HSK 7, 8 ва 9 дар рӯйхати 2025 ҳамчун як гурӯҳи умумӣ дода шудаанд.",12,GOLD,false));
      Button basics=action("Дарсҳои ибтидоии гуфтугӯ",false);
      basics.setOnClickListener(v->showBasicLessons());body.addView(basics);
    }
    void showBasicLessons(){base("Дарсҳои гуфтугӯ","Ибораҳои ҳаррӯза","lessons");
      for(int i=0;i<lessons.length;i++){final int n=i;LinearLayout r=row();r.addView(label(lessons[i][0],15,WHITE,true),new LinearLayout.LayoutParams(0,dp(48),1));r.addView(label("›",22,GOLD,true));card(body,r);r.setOnClickListener(v->showLesson(n));}
    }
    void showHskLevel(int level){
      ArrayList<String[]> chars=charsForLevel(level);base(levelName(level),"Иероглифҳо ба гурӯҳҳои 20-тоӣ ҷудо шудаанд","lessons");
      int groups=(chars.size()+19)/20;
      for(int g=0;g<groups;g++){
        final int group=g;int from=g*20,to=Math.min(from+20,chars.size());
        LinearLayout r=row();LinearLayout c=column();c.addView(label("Гурӯҳи "+(g+1),16,WHITE,true));
        c.addView(label((to-from)+" иероглиф • "+(completed.contains("hsk-"+level+"-"+g)?"Тест гузашта шуд":"Омӯзиш ва тест"),12,MUTED,false));
        r.addView(c,new LinearLayout.LayoutParams(0,-2,1));r.addView(label("›",23,GOLD,true));card(body,r);
        r.setOnClickListener(v->showHskGroup(level,group));
      }
    }
    void showHskGroup(int level,int group){
      ArrayList<String[]> chars=charsForLevel(level);int from=group*20,to=Math.min(from+20,chars.size());
      base(levelName(level)+" • "+(group+1),"Гурӯҳи омӯзишӣ","lessons");
      body.addView(label("Иероглифҳоро бинед ва талаффузи онҳоро гӯш кунед. Барои баъзе аломатҳо калима ва ҷумлаи намунавӣ низ нишон дода мешавад.",13,MUTED,false));
      ArrayList<String[]> vocab=wordbook();
      for(int i=from;i<to;i++){
        final String han=chars.get(i)[1];LinearLayout r=row();
        TextView glyph=label(han,34,LIME,true);glyph.setGravity(Gravity.CENTER);r.addView(glyph,new LinearLayout.LayoutParams(dp(70),dp(58)));
        LinearLayout c=column();c.addView(label("Иероглифи чинӣ",13,WHITE,true));c.addView(label("HSK "+chars.get(i)[0].replace("HSK",""),11,MUTED,false));
        r.addView(c,new LinearLayout.LayoutParams(0,-2,1));Button play=action("▶ Овоз",false);play.setOnClickListener(v->speak(han));r.addView(play);card(body,r);
        int shown=0;
        for(String[] w:vocab){
          if(w[0].contains(han)&&shown<2){
            LinearLayout example=column();
            example.addView(label("Калима: "+w[0]+"  •  "+w[1],16,LIME,true));
            example.addView(label("Тоҷикӣ: "+w[2]+"  |  Русский: "+w[3],12,MUTED,false));
            example.addView(label("Мисол: "+w[4],15,WHITE,true));
            example.addView(label(w[5],12,GOLD,true));
            example.addView(label(w[6]+"  /  "+w[7],11,MUTED,false));
            Button speakExample=action("▶ Гӯш кардани мисол",false);speakExample.setOnClickListener(v->speak(w[4]));example.addView(speakExample);
            card(body,example);shown++;
          }
        }
        if(shown==0)body.addView(label("Маъно ва мисол барои ин иероглиф ҳанӯз дар луғат илова нашудааст.",11,MUTED,false));
      }
      Button quiz=action("Озмоиши гурӯҳро оғоз кунед",true);quiz.setOnClickListener(v->showHskQuiz(level,group));body.addView(quiz,new LinearLayout.LayoutParams(-1,dp(52)));
    }
    void showHskQuiz(int level,int group){
      ArrayList<String[]> chars=charsForLevel(level);int from=group*20,to=Math.min(from+20,chars.size());
      ArrayList<String> pool=new ArrayList<>();for(int i=from;i<to;i++)pool.add(chars.get(i)[1]);
      Collections.shuffle(pool);final ArrayList<String> questions=new ArrayList<>(pool.subList(0,Math.min(10,pool.size())));
      final int[] pos={0},score={0};final boolean[] answered={false};
      base("Озмоиши HSK","Шакли дурусти иероглифро интихоб кунед","lessons");
      TextView counter=label("",13,MUTED,true),target=label("▶",38,LIME,true),feedback=label("",14,GOLD,true);
      counter.setGravity(Gravity.CENTER);target.setGravity(Gravity.CENTER);
      body.addView(counter);card(body,target);body.addView(label("Овозро гӯш кунед ва иероглифи шунидашударо интихоб кунед.",14,WHITE,true));
      Button replay=action("▶  Такрори овоз",false);body.addView(replay,new LinearLayout.LayoutParams(-1,dp(44)));
      LinearLayout choices=column();body.addView(choices);body.addView(feedback);
      Runnable[] draw=new Runnable[1];
      draw[0]=()->{
        choices.removeAllViews();feedback.setText("");
        if(pos[0]>=questions.size()){
          target.setText(score[0]+" / "+questions.size());
          counter.setText("Натиҷаи гурӯҳ");
          boolean passed=score[0]>=questions.size()*0.7;
          if(passed){completed.add("hsk-"+level+"-"+group);save();}
          feedback.setText(passed?"Гурӯҳ бомуваффақият анҷом ёфт.":"Барои гузаштан 70% ҷавоби дуруст лозим аст.");
          Button again=action("Боз санҷидан",true);again.setOnClickListener(v->showHskQuiz(level,group));choices.addView(again);
          Button back=action("Ба гурӯҳ баргаштан",false);back.setOnClickListener(v->showHskGroup(level,group));choices.addView(back);return;
        }
        String answer=questions.get(pos[0]);target.setText("▶");replay.setOnClickListener(v->speak(answer));speak(answer);counter.setText("Саволи "+(pos[0]+1)+" аз "+questions.size());
        ArrayList<String> opts=new ArrayList<>();opts.add(answer);
        while(opts.size()<4){String candidate=pool.get((int)(Math.random()*pool.size()));if(!opts.contains(candidate))opts.add(candidate);}
        Collections.shuffle(opts);
        for(String opt:opts){Button b=action(opt,false);b.setTextSize(22);b.setOnClickListener(v->{if(answered[0])return;answered[0]=true;
          if(opt.equals(answer)){score[0]++;feedback.setText("Дуруст.");}else feedback.setText("Ҷавоби дуруст: "+answer);
          Button next=action("Саволи навбатӣ  →",true);choices.addView(next);next.setOnClickListener(x->{pos[0]++;answered[0]=false;draw[0].run();});
        });choices.addView(b,new LinearLayout.LayoutParams(-1,dp(48)));View gap=new View(this);choices.addView(gap,new LinearLayout.LayoutParams(1,dp(6)));}
      };
      draw[0].run();
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
    ArrayList<String[]> wordbook(){
      ArrayList<String[]> out=new ArrayList<>();
      try(BufferedReader br=new BufferedReader(new InputStreamReader(getAssets().open("hsk_wordbook.tsv"),"UTF-8"))){
        String line;boolean first=true;
        while((line=br.readLine())!=null){
          if(first){first=false;continue;}
          String[] p=line.split("\\t",-1);
          if(p.length>=8)out.add(p);
        }
      }catch(Exception e){Toast.makeText(this,"Луғат бор нашуд: "+e.getMessage(),Toast.LENGTH_LONG).show();}
      return out;
    }
    ArrayList<String[]> officialVocabulary(){
      ArrayList<String[]> out=new ArrayList<>();
      try(BufferedReader br=new BufferedReader(new InputStreamReader(getAssets().open("hsk_vocabulary.tsv"),"UTF-8"))){
        String line;boolean first=true;
        while((line=br.readLine())!=null){
          if(first){first=false;continue;}
          String[] p=line.split("\\t",-1);
          if(p.length>=6)out.add(p);
        }
      }catch(Exception e){Toast.makeText(this,"HSK 3.0: "+e.getMessage(),Toast.LENGTH_LONG).show();}
      return out;
    }
    void showDictionary(){dictionaryScreen(false);}
    void dictionaryScreen(boolean onlyFav){
      base(onlyFav?"Калимаҳои интихобшуда":"Луғати HSK 3.0",onlyFav?"Калимаҳои нигоҳдошта":"Ҷустуҷӯ дар рӯйхати пурраи 11 000 калима","dict");
      EditText search=new EditText(this);search.setSingleLine(true);search.setTextColor(WHITE);search.setHintTextColor(MUTED);
      search.setHint("⌕  Калимаи чинӣ, Pinyin ё тарҷумаро нависед");search.setTextSize(14);search.setPadding(dp(14),dp(10),dp(14),dp(10));
      search.setBackground(shape(PANEL2,14,GOLD));body.addView(search);
      body.addView(label("HSK 3.0 • 11 000 калима • 7 гурӯҳ • ҷустуҷӯи офлайн",12,GOLD,false));
      LinearLayout results=column();body.addView(results);
      ArrayList<String[]> official=officialVocabulary();ArrayList<String[]> curated=wordbook();
      HashMap<String,String[]> translated=new HashMap<>();for(String[] w:curated)translated.put(w[0],w);
      TextView status=label("Барои дидани калимаҳо дар сатри ҷустуҷӯ ҳарф ё Pinyin ворид кунед.",13,MUTED,false);body.addView(status,1);
      Runnable render=()->{
        results.removeAllViews();String q=search.getText().toString().toLowerCase(Locale.ROOT).trim();
        int count=0,shown=0;
        for(String[] w:official){
          String word=w[1],pinyin=w[2],pos=w[3],level=w[0];
          String[] extra=translated.get(word);
          if(onlyFav&&!favorites.contains(word))continue;
          String hay=(word+" "+pinyin+" "+pos+" HSK "+(level.equals("7")?"7-9":level)).toLowerCase(Locale.ROOT);
          if(extra!=null)hay+=" "+extra[2]+" "+extra[3]+" "+extra[4]+" "+extra[6]+" "+extra[7];
          if(!q.isEmpty()&&!hay.contains(q))continue;
          count++;if(shown>=80)continue;shown++;
          String shownWord=word.replaceAll("\\d+$","");
          LinearLayout c=column();c.addView(label(shownWord,27,LIME,true));c.addView(label(pinyin,14,WHITE,true));
          c.addView(label("HSK "+(level.equals("7")?"7–9":level)+"  •  "+pos,11,GOLD,false));
          if(extra!=null){
            c.addView(label("Тоҷикӣ: "+extra[2],13,MUTED,false));c.addView(label("Русский: "+extra[3],13,MUTED,false));
            gap(c,5);c.addView(label("Мисол: "+extra[4],16,WHITE,true));c.addView(label(extra[5],12,GOLD,true));
            c.addView(label("Тоҷикӣ: "+extra[6],12,MUTED,false));c.addView(label("Русский: "+extra[7],12,MUTED,false));
          }else{
            c.addView(label("Тарҷума ва ҷумлаи намунавӣ ҳоло ба ин калима илова нашудааст.",12,MUTED,false));
          }
          LinearLayout acts=row();Button play=action("▶  Овоз",false);play.setOnClickListener(v->speak(shownWord));acts.addView(play,new LinearLayout.LayoutParams(0,dp(40),1));
          Button fav=action(favorites.contains(word)?"★":"☆",false);fav.setOnClickListener(v->{toggleFavorite(word);dictionaryScreen(onlyFav);});LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(dp(55),dp(40));fp.leftMargin=dp(8);acts.addView(fav,fp);c.addView(acts);card(results,c);
        }
        status.setText(q.isEmpty()?"Барои дидани калимаҳо ҷустуҷӯро оғоз кунед.":"Ёфт шуд: "+count+" • Нишон дода мешавад: "+shown+(count>shown?" • натиҷаҳои дигарро бо дақиқтар кардани ҷустуҷӯ бинед.":""));
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