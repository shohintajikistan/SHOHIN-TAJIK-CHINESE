package tj.shohin.chinese;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import android.speech.tts.TextToSpeech;
import java.util.*;

public class MainActivity extends Activity {
    final int BG=Color.rgb(7,28,22), PANEL=Color.rgb(13,48,36), GREEN=Color.rgb(8,165,108), GOLD=Color.rgb(186,240,91), WHITE=Color.rgb(245,247,242), MUTED=Color.rgb(173,193,181);
    LinearLayout root, body;
    TextToSpeech tts;
    boolean ttsReady=false;
    int completed;
    final String[][] lessons = {
        {"1","Салом ва шиносоӣ","你好","nǐ hǎo","Салом","谢谢","xièxie","Ташаккур","再见","zàijiàn","То дидор","我","wǒ","Ман","你","nǐ","Ту / Шумо"},
        {"2","Рақамҳо","一","yī","Як","二","èr","Ду","三","sān","Се","四","sì","Чор","五","wǔ","Панҷ"},
        {"3","Оила","爸爸","bàba","Падар","妈妈","māma","Модар","哥哥","gēge","Бародари калон","姐姐","jiějie","Хоҳари калон","家","jiā","Хона / Оила"},
        {"4","Хӯрок ва нӯшокӣ","水","shuǐ","Об","茶","chá","Чой","米饭","mǐfàn","Биринҷ / хӯрок","面条","miàntiáo","Угро","好吃","hǎochī","Бомаза"},
        {"5","Савдо ва харид","多少钱？","duōshao qián?","Чанд пул?","太贵了","tài guì le","Хеле қимат","便宜一点","piányi yìdiǎn","Каме арзонтар","买","mǎi","Харидан","这个","zhège","Ин"},
        {"6","Ҷумлаҳои муҳим","我不懂","wǒ bù dǒng","Ман намефаҳмам","请再说一次","qǐng zài shuō yí cì","Лутфан такрор кунед","厕所在哪里？","cèsuǒ zài nǎlǐ?","Ҳоҷатхона дар куҷост?","我是塔吉克斯坦人","wǒ shì Tǎjíkèsītǎn rén","Ман тоҷикистониям","帮助我","bāngzhù wǒ","Ба ман ёрӣ диҳед"}
    };
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(BG);
        completed=getPreferences(0).getInt("completed",0);
        tts=new TextToSpeech(this, status->{ if(status==TextToSpeech.SUCCESS){ int r=tts.setLanguage(Locale.SIMPLIFIED_CHINESE); ttsReady=(r!=TextToSpeech.LANG_MISSING_DATA && r!=TextToSpeech.LANG_NOT_SUPPORTED); }});
        showHome();
    }
    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density);}
    TextView text(String s,int size,int color,boolean bold){
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setGravity(Gravity.CENTER_VERTICAL);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); t.setPadding(0,dp(3),0,dp(3)); return t;
    }
    GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    void base(String title,String subtitle){
        root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(BG);setContentView(root);
        LinearLayout top=new LinearLayout(this);top.setOrientation(1);top.setPadding(dp(20),dp(18),dp(20),dp(14));top.setBackground(bg(PANEL,0));
        top.addView(text("♛  SHOHIN TAJIKISTAN",13,GOLD,true));top.addView(text(title,25,WHITE,true));top.addView(text(subtitle,13,MUTED,false));root.addView(top);
        ScrollView scroll=new ScrollView(this);body=new LinearLayout(this);body.setOrientation(1);body.setPadding(dp(18),dp(16),dp(18),dp(24));scroll.addView(body);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout nav=new LinearLayout(this);nav.setPadding(dp(8),dp(8),dp(8),dp(8));nav.setBackgroundColor(PANEL);
        Button home=navButton("⌂  Дарсҳо");home.setOnClickListener(v->showHome());nav.addView(home,new LinearLayout.LayoutParams(0,dp(48),1));
        Button quiz=navButton("✓  Санҷиш");quiz.setOnClickListener(v->showQuiz());nav.addView(quiz,new LinearLayout.LayoutParams(0,dp(48),1));root.addView(nav);
    }
    Button navButton(String s){Button b=new Button(this);b.setText(s);b.setTextColor(WHITE);b.setTextSize(12);b.setAllCaps(false);b.setBackgroundColor(Color.TRANSPARENT);return b;}
    void card(LinearLayout parent,View child){LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setPadding(dp(15),dp(13),dp(15),dp(13));box.setBackground(bg(PANEL,16));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(11);parent.addView(box,p);box.addView(child);}
    void showHome(){
        base("Чиниро аз сифр омӯзед","Тоҷикӣ → 中文  |  Қадам ба қадам");
        LinearLayout hero=new LinearLayout(this);hero.setOrientation(1);hero.setPadding(dp(18),dp(17),dp(18),dp(17));hero.setBackground(bg(Color.rgb(10,65,46),18));
        hero.addView(text("你好  nǐ hǎo",30,GOLD,true));hero.addView(text("Салом! Ин аввалин ибораи шумост ба забони чинӣ.",15,WHITE,false));hero.addView(text("Пешрафт: "+completed+" аз "+lessons.length+" дарс",13,MUTED,true));body.addView(hero);
        TextView intro=text("Дарсҳои ибтидоӣ",19,WHITE,true);intro.setPadding(0,dp(18),0,dp(10));body.addView(intro);
        for(int i=0;i<lessons.length;i++){final int index=i;LinearLayout item=new LinearLayout(this);item.setOrientation(1);item.addView(text("ДАРС "+lessons[i][0]+"   •   "+lessons[i][1],17,WHITE,true));item.addView(text("Иероглифҳо, талаффуз ва тарҷумаи тоҷикӣ",13,MUTED,false));card(body,item);item.setOnClickListener(v->showLesson(index));}
        body.addView(text("Маслиҳат: ҳар рӯз як дарсро омӯзед ва калимаҳоро бо овоз такрор кунед.",13,GOLD,false));
    }
    void showLesson(int index){
        String[] l=lessons[index];base("Дарси "+l[0],l[1]);body.addView(text("Гӯш кунед, хонед ва такрор кунед.",14,MUTED,false));
        for(int j=2;j<l.length;j+=3){final String han=l[j], pin=l[j+1], taj=l[j+2];LinearLayout item=new LinearLayout(this);item.setOrientation(1);item.addView(text(han,30,GOLD,true));item.addView(text(pin,17,WHITE,true));item.addView(text(taj,15,MUTED,false));
            Button speak=new Button(this);speak.setText("Гӯш кардани талаффуз");speak.setAllCaps(false);speak.setTextColor(WHITE);speak.setBackgroundTintList(android.content.res.ColorStateList.valueOf(GREEN));
            speak.setOnClickListener(v->{if(ttsReady)tts.speak(han,TextToSpeech.QUEUE_FLUSH,null,"word");else Toast.makeText(this,"Овози чинӣ дар дастгоҳ дастрас нест. Pinyin-ро хонед.",Toast.LENGTH_LONG).show();});item.addView(speak);card(body,item);}
        Button done=new Button(this);done.setText("Дарсро анҷом додам ✓");done.setAllCaps(false);done.setTextColor(BG);done.setTextSize(16);done.setBackgroundTintList(android.content.res.ColorStateList.valueOf(GOLD));
        done.setOnClickListener(v->{completed=Math.min(lessons.length,completed+1);getPreferences(0).edit().putInt("completed",completed).apply();Toast.makeText(this,"Офарин! Пешрафт нигоҳ дошта шуд.",Toast.LENGTH_SHORT).show();showHome();});body.addView(done);
    }
    void showQuiz(){
        base("Санҷиши кӯтоҳ","Маънои калимаи чиниро интихоб кунед.");
        String[] q={"你好","谢谢","水","爸爸","多少钱？"};String[][] a={{"Салом","Хайр","Об"},{"Ташаккур","Лутфан","Бубахшед"},{"Чой","Об","Хӯрок"},{"Модар","Падар","Хоҳар"},{"Ин чист?","Чанд пул?","Куҷо?"}};int[] correct={0,0,1,1,1};
        final int[] score={0};final int[] pos={0};TextView question=text("",25,GOLD,true);body.addView(question);TextView counter=text("",14,MUTED,false);body.addView(counter);LinearLayout choices=new LinearLayout(this);choices.setOrientation(1);body.addView(choices);
        Runnable[] render=new Runnable[1];render[0]=()->{choices.removeAllViews();if(pos[0]>=q.length){question.setText("Натиҷа: "+score[0]+" / "+q.length);counter.setText("Санҷиш ба анҷом расид.");return;}
            question.setText(q[pos[0]]);counter.setText("Саволи "+(pos[0]+1)+" аз "+q.length);
            for(int k=0;k<3;k++){final int pick=k;Button b=new Button(this);b.setText(a[pos[0]][k]);b.setAllCaps(false);b.setTextColor(WHITE);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(PANEL));choices.addView(b);b.setOnClickListener(v->{if(pick==correct[pos[0]])score[0]++;pos[0]++;render[0].run();});}
        };render[0].run();
    }
    @Override public void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}
