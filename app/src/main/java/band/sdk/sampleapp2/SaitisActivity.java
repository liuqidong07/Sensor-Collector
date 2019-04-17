package band.sdk.sampleapp2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import band.sdk.sampleapp.R;

public class SaitisActivity extends Activity implements OnChartValueSelectedListener {
    private LineChart lineChart;
    String filepath;

    private List<Float> list=new ArrayList<Float>();
    private List<Float> list1=new ArrayList<Float>();
    private List<Float> list2=new ArrayList<Float>();

    private List<Float> list11=new ArrayList<Float>();
    private List<Float> list12=new ArrayList<Float>();
    private List<Float> list23=new ArrayList<Float>();
    private List<Long> listX=new ArrayList<Long>();
    private   PieChart mChart;
    private  BarChart barChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_statis);

        Intent intent=getIntent();
        filepath = intent.getStringExtra("path");
        inintView();
        inintChart();
        configBarChart();
    }
private Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    initLineChart(list11);
                    setData(list11);
                    initBarChart(list11);
                    break;
            }
    }
};

    public  void configBarChart() {
        barChart = (BarChart)findViewById(R.id.barChart);
        barChart.getDescription().setEnabled(false);//设置描述
        barChart.setPinchZoom(false);//设置按比例放缩柱状图
        barChart.setScaleEnabled(false);
        barChart.setDragEnabled(true);
        barChart.setNoDataText(""); // 没有数据时的提示文案
        //x坐标轴设置
        // IAxisValueFormatter xAxisFormatter = new StringAxisValueFormatter(xAxisValue);//设置自定义的x轴值格式化器
        XAxis xAxis = barChart.getXAxis();//获取x轴
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴标签显示位置
        xAxis.setDrawGridLines(false);//不绘制格网线
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。

        xAxis.setTextSize(10);//设置标签字体大小
        xAxis.setAxisLineColor(Color.parseColor("#4cffffff"));

        //y轴设置
        YAxis leftAxis = barChart.getAxisLeft();//获取左侧y轴
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);//设置y轴标签显示在外侧
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(true);//禁止绘制y轴标签
        leftAxis.setAxisLineColor(Color.parseColor("#4cffffff"));
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return ((int) (value )) + "";
            }
        });

        barChart.getAxisRight().setEnabled(false);//禁用右侧y轴


        Matrix matrix = new Matrix();
            matrix.postScale(2.0f, 1.0f);
        barChart.getViewPortHandler().refresh(matrix, barChart, false);
        barChart.setExtraBottomOffset(10);//距视图窗口底部的偏移，类似与paddingbottom
        barChart.setExtraTopOffset(30);//距视图窗口顶部的偏移，类似与paddingtop
        barChart.setFitBars(true);//使两侧的柱图完全显示
        barChart.animateX(1500);//数据显示动画，从左往右依次显示
    }


    private void inintChart() {
        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        mChart.setDrawHoleEnabled(false);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setEntryLabelColor(Color.BLACK);

        mChart.setEntryLabelTextSize(10f);
        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        mChart.setOnChartValueSelectedListener(this);

        Legend legend = mChart.getLegend();
        legend.setEnabled(false);//是否显示图例
    }


    private void inintView() {
        lineChart= (LineChart) findViewById(R.id.lchart);
   new Runnable() {
       @Override
       public void run() {
           readFileByLines(filepath);

       }
   }.run();

findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        initLineChart(list11);
        setData(list11);
        initBarChart(list11);
    }
});
        findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initLineChart(list12);
                setData(list12);
                initBarChart(list12);
            }
        });
        findViewById(R.id.tv3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initLineChart(list23);
                setData(list23);
                initBarChart(list23);
            }
        });
    }
    public  void readFileByLines(String fileName) {
        list.clear();
        listX.clear();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
           //一次读一行，读入null时文件结束
            while ((tempString = reader.readLine()) != null&&line<2000) {
             //把当前行号显示出来
            String[]  strings=  tempString.split(" ");
                if (!"begin".equals(strings[3])&&!"stop".equals(strings[3])){
                    if (!TextUtils.isEmpty(strings[0])){
                        list.add(Float.parseFloat(strings[0]));
                    }else{
                        list.add(0f);
                    }
                    if (!TextUtils.isEmpty(strings[1])){
                        list1.add(Float.parseFloat(strings[1]));
                    }else{
                        list1.add(0f);
                    }
                    if (!TextUtils.isEmpty(strings[2])){
                        list2.add(Float.parseFloat(strings[2]));
                    }else{
                        list2.add(0f);
                    }
                    if (isNumeric(strings[3])){
                        listX.add(Long.parseLong(strings[3]));
                    }else{
                        listX.add((long) 0);
                    }
                }
                line++;
            }
            if (list.size()>20){
                for (int i = 0; i < list.size(); i=i+list.size()/20+1) {
                    list11.add(list.get(i));
                }
            } if (list1.size()>20){
                for (int i = 0; i < list1.size(); i=i+list1.size()/20+1) {
                    list12.add(list1.get(i));
                }
            } if (list2.size()>20){
                for (int i = 0; i < list2.size(); i=i+list2.size()/20+1) {
                    list23.add(list2.get(i));
                }
            }
           handler.sendEmptyMessage(100);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    /**
     * 初始化柱状图图表数据
     */
    public  void initBarChart(List<Float> list1c) {
List<BarEntry> barDataSets=new ArrayList<BarEntry>();
        for (int i = 0; i <list1c.size() ; i++) {
            barDataSets.add(new BarEntry(i,list1c.get(i)));
        }

        BarDataSet set1 = new BarDataSet(barDataSets, "");
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);

        //设置y轴显示的标签
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return value + "";
            }
        });

        barChart.setData(data);
        barChart.invalidate();
    }


    private void setData(List<Float> listp) {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        for (int i = 0; i <listp.size() ; i=i+2) {
            entries.add(new PieEntry(listp.get(i)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);

        mChart.highlightValues(null);

        mChart.invalidate();
    }


    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    private void initLineChart(final List<Float> list)
    {
        //显示边界
        lineChart.setDrawBorders(false);
        //设置数据
        List<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < list.size(); i++)
        {
            entries.add(new Entry(i, (float) list.get(i)));
        }
        //一个LineDataSet就是一条线
        LineDataSet lineDataSet = new LineDataSet(entries, "");
        //线颜色
        lineDataSet.setColor(Color.parseColor("#F15A4A"));
        //线宽度
        lineDataSet.setLineWidth(1.6f);
        //不显示圆点
        lineDataSet.setDrawCircles(false);
        //线条平滑
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        //设置折线图填充
        lineDataSet.setDrawFilled(true);
        LineData data = new LineData(lineDataSet);
        //无数据时显示的文字
        lineChart.setNoDataText("暂无数据");
        //折线图不显示数值
        data.setDrawValues(false);
        //得到X轴
        XAxis xAxis = lineChart.getXAxis();
       //设置X轴的位置（默认在上方)
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //得到Y轴
        YAxis yAxis = lineChart.getAxisLeft();
        YAxis rightYAxis = lineChart.getAxisRight();
        //设置Y轴是否显示
        rightYAxis.setEnabled(false); //右侧Y轴不显示
        //设置y轴坐标之间的最小间隔
        //不显示网格线
        yAxis.setDrawGridLines(false);
        //设置Y轴坐标之间的最小间隔
        yAxis.setGranularity( 0.1f);

        //图例：得到Lengend
        Legend legend = lineChart.getLegend();
        //隐藏Lengend
        legend.setEnabled(false);
        //隐藏描述
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);
        //设置数据
        lineChart.setData(data);
        //图标刷新
        lineChart.invalidate();
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
