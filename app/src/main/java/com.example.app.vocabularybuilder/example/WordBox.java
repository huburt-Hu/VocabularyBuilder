package com.example.app.vocabularybuilder.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by hxb on 2016/5/7.
 */
public class WordBox {
    public Context context=null;
    public String tableName=null;
    private DataBaseHelper dbHelper=null;
    private SQLiteDatabase dbR=null,dbW=null;

    public final static int GRASP_89=8;
    public final static int GRASP_67=6;
    public final static int GRASP_45=4;
    public final static int GRASP_23=2;
    public final static int GRASP_01=0;
    public final static int LEARN_NEW_WORD=10;
    public final static int LEARNED=1;
    public final static int UNLEARNED=0;



    public static int process=GRASP_89;    //总学习进度控制变量
    public static int wordCount=0;         //在某一复习阶段背的单词数
    public static boolean processWrong=false;        //是否要开始背错误的单词

    public final static int STEP_1_NEWWORD1=0;
    public final static int STEP_2_REVIEW_20=1;
    public final static int STEP_3_NEWWORD2=2;
    public final static int STEP_4_REVIEW_6=3;
    public static int processLearnNewWord=STEP_1_NEWWORD1;


    public LinkedList<WordInfo> wrongWordList=null;
    public Random rand=null;


    public WordBox(Context context,String tableName){
        this.context=context;
        this.tableName=tableName;
        dbHelper=new DataBaseHelper(context, tableName);
        dbR=dbHelper.getReadableDatabase();
        dbW=dbHelper.getWritableDatabase();
        wrongWordList=new LinkedList<WordInfo>();
        rand=new Random();
    }



    @Override
    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        dbR.close();
        dbW.close();
        dbHelper.close();
        super.finalize();
    }



    public void removeWordFromDatabase(String word){
        dbW.delete(tableName, "word=?", new String[]{word});
    }

    /**
     * 多个条件查找Where子句时需要用and 或or连接
     * @param grasp
     * @param learned
     * @return
     */

    public int getWordCountByGrasp(int grasp ,int learned){       //获得数据库中某个掌握程度的单词的个数
        Cursor cursor=dbR.query(tableName, new String[]{"word"}, "grasp=? and learned=?", new String[]{grasp+"",learned+""}, null, null, null);
        int count=cursor.getCount();
        cursor.close();
        return count;
    }

    public int getTotalLearnProgress(){
        int learnCount=0;
        int totalCount=0;
        Cursor cursor=dbR.query(tableName, new String[]{"word"}, "grasp=? or grasp=? or grasp=? or grasp=? or grasp=? or grasp=? or grasp=? or grasp=?", new String[]{"3","4","5","6","7","8","9","10"}, null, null, null);
        learnCount=cursor.getCount();

        Cursor cursorTotal=dbR.query(tableName, new String[]{"word"}, "word like?", new String[]{"%"}, null, null, null);
        totalCount=cursorTotal.getCount();

        cursor.close();
        cursorTotal.close();
        if(totalCount==0){
            return 0;
        }
        return  (int)(((float)learnCount/(float)totalCount)*100);
    }

    public int getWordCountOfUnlearned(){
        Cursor cursorTotal=dbR.query(tableName, new String[]{"word"}, "word like?", new String[]{"%"}, null, null, null);
        int totalCount=cursorTotal.getCount();
        Cursor cursor=dbR.query(tableName, new String[]{"word"}, "grasp=? or grasp=? or grasp=? or grasp=? or grasp=? or grasp=? or grasp=? or grasp=?", new String[]{"3","4","5","6","7","8","9","10"}, null, null, null);
        int learnCount=cursor.getCount();
        cursor.close();
        cursorTotal.close();
        return totalCount-learnCount;
    }

    public WordInfo getWordByGraspByRandom(int fromGrasp,int toGrasp,int learned){    //从数据库中随机取出某个特定掌握程度区间的单词,加learned是区别学习程度为0的学过得和没学过的
        int totalCount=0,temp=0;
        ArrayList<Integer> graspsNotEmpty=new ArrayList<Integer>();
        for(int i=fromGrasp; i<=toGrasp;i++){
            temp=getWordCountByGrasp(i,learned);  //这说明给定掌握程度范围内没有单词
            totalCount+=temp;
            if(temp>0)
                graspsNotEmpty.add(i);  //把对应的grasp添加
        }
        if(totalCount<=0){        //这里应该在外部添加判断空的表不能够进来
            return null;
        }

        int length=graspsNotEmpty.size();
        if(length<=0)
            return null;       //在有数据的掌握程度中随机找出一个单词来
        int graspInt=graspsNotEmpty.get(rand.nextInt(length));     //随机确定一个掌握程度
        int count=getWordCountByGrasp(graspInt, learned);          //确定该掌握程度单词数，获得Cursor对象，利用move方法进行随机移动
        int index=rand.nextInt(count)+1;
        Cursor cursor=dbR.query(tableName, new String[]{"word","interpret","right","wrong","grasp"},"grasp=? and learned=?" , new String[]{graspInt+"",learned+""}, null, null, null);
        cursor.move(index);
        String word=cursor.getString(cursor.getColumnIndex("word"));
        String interpret=cursor.getString(cursor.getColumnIndex("interpret"));
        int wrong=cursor.getInt(cursor.getColumnIndex("wrong"));
        int right=cursor.getInt(cursor.getColumnIndex("right"));
        int grasp=cursor.getInt(cursor.getColumnIndex("grasp"));
        cursor.close();
        return new WordInfo(word, interpret, wrong, right, grasp);
    }


    /**
     * 随机从词库中找一个单词！
     */
    public static int lastGetIndex=0;

    public WordInfo getWordByRandom(){
        int count=0;
        Cursor cursor=dbR.query(tableName, new String[]{"word","interpret","right","wrong","grasp"},"word like?" ,
                new String[]{"%"}, null, null, null);
        if((count=cursor.getCount())<=0){
            cursor.close();
            return null;
        }

        int i=0;
        int index=0;
        while(i<6){
            index=rand.nextInt(count)+1;
            if(index!=lastGetIndex)
                break;
            i++;
        }



        lastGetIndex=index;
        cursor.move(index);
        String word=cursor.getString(cursor.getColumnIndex("word"));
        String interpret=cursor.getString(cursor.getColumnIndex("interpret"));
        int wrong=cursor.getInt(cursor.getColumnIndex("wrong"));
        int right=cursor.getInt(cursor.getColumnIndex("right"));
        int grasp=cursor.getInt(cursor.getColumnIndex("grasp"));
        cursor.close();
        return new WordInfo(word, interpret, wrong, right, grasp);
    }

    String[] logProcess=new String[]{"G01","","G23","","G45","","G67","","G89","","NEW WORD"};
    String[] logLearn=new String[]{"NEW1","REVIEW20","NEW2","REVIEW6"};

    //外部接口，点击事件后获得单词
    public WordInfo popWord(){
        WordInfo wordInfo=null;

        /**
         * 打印参数信息
         */

        if(processWrong){
            return getWrongWord();
        }
        switch(process){
            case GRASP_89:{

                if((wordInfo=getWordByAccurateGrasp(GRASP_89, GRASP_67,0.1))!=null)
                    return wordInfo;
            }
            case GRASP_67:{
                if((wordInfo=getWordByAccurateGrasp(GRASP_67, GRASP_45,0.3))!=null)
                    return wordInfo;
            }
            case GRASP_45:{
                if((wordInfo=getWordByAccurateGrasp( GRASP_45,GRASP_23,0.4))!=null)
                    return wordInfo;
            }
            case GRASP_23:{
                if((wordInfo=getWordByAccurateGrasp(GRASP_23, GRASP_01,0.5))!=null)
                    return wordInfo;
            }
            case GRASP_01:{
                if((wordInfo=getWordByAccurateGrasp(GRASP_01,LEARN_NEW_WORD,0.5))!=null)
                    return wordInfo;
            }
            case LEARN_NEW_WORD:{
                return learnNewWord();
            }
            default:
                break;

        }
        return null;
    }


    //外部敲击后反馈回来的函数
    public void feedBack(WordInfo wordInfo,boolean isRight){
        if(wordInfo==null)
            return;           //对可能出现的空指针异常进行处理

        String word=wordInfo.getWord();
        int right=wordInfo.getRight();
        int wrong=wordInfo.getWrong();
        int graspInt=0;
        if(isRight){
            right++;
        }else{
            wrong++;      //更新答对答错次数
        }
        if(right-2*wrong<0){
            graspInt=0;
        }else if(right-2*wrong>10){
            graspInt=10;
        }else{
            graspInt=right-2*wrong;
        }

        //更新数据库
        ContentValues values=new ContentValues();
        //更新应该只会更新添加的项吧，暂时这么处理
        values.put("right", right);
        values.put("wrong",wrong);
        values.put("grasp",graspInt);
        values.put("learned", LEARNED);
        dbW.update(tableName, values, "word=?", new String[]{word});

        //若出错，将数据存在出错队列中
        if(isRight==false){
            wordInfo.setRight(right);
            wordInfo.setWrong(wrong);
            wordInfo.setGrasp(graspInt);
            wrongWordList.offer(wordInfo);
        }

    }


    //新词学习阶段调用的函数
    public WordInfo learnNewWord(){
        //这里设置一个彩蛋
        WordInfo wordInfo=null;

        switch(processLearnNewWord){
            case STEP_1_NEWWORD1:{
                if((wordInfo=getWordByGraspByRandom(GRASP_01,GRASP_01,UNLEARNED ))==null
                        || wordCount>rand.nextInt(3)+9 ){
                    processLearnNewWord=STEP_2_REVIEW_20;
                    wordCount=0;

                    //这里表示所有的词都已经学完了
                    if(getWordCountByGrasp(GRASP_01, UNLEARNED)<=0){
                        process=GRASP_89;
                    }
                }else{
                    wordCount++;
                    return wordInfo;
                }
            }
            case STEP_2_REVIEW_20:{
                if((wordInfo=getWordByGraspByRandom(0,2, LEARNED))==null){
                    processLearnNewWord=STEP_3_NEWWORD2;
                    wordCount=0;
                }else{
                    wordCount++;
                    if(wordCount>rand.nextInt(3)+19){
                        processLearnNewWord=STEP_3_NEWWORD2;
                        wordCount=0;
                        if(wrongWordList.size()>0)
                            processWrong=true;
                    }
                    return wordInfo;
                }
            }
            case STEP_3_NEWWORD2:{
                if((wordInfo=getWordByGraspByRandom(GRASP_01,GRASP_01,UNLEARNED ))==null
                        || wordCount>rand.nextInt(3)+9 ){
                    processLearnNewWord=STEP_4_REVIEW_6;
                    wordCount=0;
                }else{
                    wordCount++;
                    return wordInfo;
                }

            }
            case STEP_4_REVIEW_6:{

                if((wordInfo=getWordByGraspByRandom(0,2, LEARNED))==null){
                    processLearnNewWord=STEP_1_NEWWORD1;
                    wordCount=0;
                    /**
                     * 这里必须返回一个非空值，否则程序将面临空指针异常（会执行default）
                     * 解决这个问题的方法是从数据库中随机取一个单词填坑。
                     */
                    return getWordByRandom();


                }else{
                    wordCount++;
                    if(wordCount>rand.nextInt(3)+5){
                        processLearnNewWord=STEP_1_NEWWORD1;
                        wordCount=0;
                        if(wrongWordList.size()>0)
                            processWrong=true;
                    }
                    return wordInfo;
                }

            }
            default: return null;

        }


    }

    //复习阶段调用的取词函数
    public WordInfo getWordByAccurateGrasp(int curentGrasp,int nextGrasp,double percent){
        int count=0;
        if((count=getWordCountByGrasp(curentGrasp,LEARNED)+getWordCountByGrasp(curentGrasp+1,LEARNED))<=0 || wordCount>=count*percent){
            process=nextGrasp;
            wordCount=0;
            return null;
        }else{
            wordCount++;

            if(wordCount%(rand.nextInt(2)+19) ==0 && wrongWordList.size()>0 ){  //错误列表中必须有单词
                processWrong=true;
            }
            /**
             * return getWordByGraspByRandom(rand.nextInt(2)+curentGrasp,LEARNED );
             * 这样写会可能返回空值！需要逐个排除
             */
            return getWordByGraspByRandom(curentGrasp,curentGrasp+1, LEARNED);


        }
    }

    //学习错词的函数
    public WordInfo getWrongWord(){  //该函数被调用时，意味着错误词列表中一定有单词
        WordInfo word=null;
        word=wrongWordList.poll();
        if(wrongWordList.size()<=0){
            processWrong=false;    //停止显示错词
        }
        return word;
    }

}

