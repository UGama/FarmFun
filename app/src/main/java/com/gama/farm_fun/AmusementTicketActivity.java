package com.gama.farm_fun;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static android.animation.ObjectAnimator.ofFloat;

public class AmusementTicketActivity extends AppCompatActivity implements View.OnClickListener {
    public int screenWidth;
    public int screenHeight;

    private String userId;
    private String type;
    private String url;
    private String projectName;

    private RecyclerView ticketRecyclerView;
    private List<Ticket> ticketList;

    private View topBar;
    private TextView title;
    private Button back;

    private View topPanel;
    private TextView topPanelChosenDate;

    private View timeChosePanel;
    private ImageView shelter;
    private Button cancelDateChose;
    private RecyclerView monthRecyclerView;
    private List<Date> dateList;
    private List<Month> monthList;
    private AnimatorSet timeChosePanelQuit;

    private String time;

    private AVObject orderAVObject;
    private String orderAmusementType;
    private int orderPrice;
    private String orderDetail;

    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amusement_ticket);

        Intent intent = getIntent();

        type = intent.getStringExtra("Type");
        Log.i("getType", String.valueOf(type));
        projectName = intent.getStringExtra("Project");
        userId = intent.getStringExtra("UserId");
        url = intent.getStringExtra("Url");
        getWindowInformation();
    }

    public void getWindowInformation() {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;         // 屏幕宽度（像素）
        screenHeight = displayMetrics.heightPixels;       // 屏幕高度（像素）
        float density = displayMetrics.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = displayMetrics.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        float width = screenWidth / density;  // 屏幕宽度(dp)
        float height = screenHeight / density;// 屏幕高度(dp)
        Log.i("width/height(px)", String.valueOf(screenWidth) + "/" + String.valueOf(screenHeight));
        Log.i("width/height(dp)", String.valueOf(width) + "/" + String.valueOf(height));
        initUI();
    }

    public void initUI() {
        ticketRecyclerView = findViewById(R.id.seatRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ticketRecyclerView.setLayoutManager(linearLayoutManager);
        ticketList = new ArrayList<>();

        topBar = findViewById(R.id.bar_top);
        title = topBar.findViewById(R.id.title);
        title.setText(projectName);
        back = topBar.findViewById(R.id.back);
        back.setOnClickListener(this);

        topPanel = findViewById(R.id.timeChosePanel);
        topPanel.setOnClickListener(this);
        topPanelChosenDate = topPanel.findViewById(R.id.chosenDate);
        topPanelChosenDate.setText("2019年06月01日");
        time = topPanelChosenDate.getText().toString();

        timeChosePanel = findViewById(R.id.datePanel);
        shelter = findViewById(R.id.shelter);
        shelter.setOnClickListener(this);
        cancelDateChose = timeChosePanel.findViewById(R.id.cancel);
        cancelDateChose.setOnClickListener(this);
        monthRecyclerView = findViewById(R.id.timeChoseRecyclerView);
        dateList = new ArrayList<>();
        monthList = new ArrayList<>();
        ObjectAnimator delay = ofFloat(timeChosePanel, "rotation", 0, 0);
        delay.setDuration(500);
        ObjectAnimator quit = ofFloat(timeChosePanel, "translationY", 0, screenHeight);
        quit.setDuration(500);
        timeChosePanelQuit = new AnimatorSet();
        timeChosePanelQuit.play(delay).before(quit);
        getTicketInformation();
    }

    public void getTicketInformation() {
        AVQuery<AVObject> query = new AVQuery<>("Ticket");
        query.whereEqualTo("amusementType", type);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    Log.i("type", avObject.getString("name") + avObject.getString("type") + String.valueOf(avObject.getInt("price") + String.valueOf(avObject.getInt("sales"))));
                    Ticket ticket = new Ticket(avObject.getString("name"), avObject.getString("type"),
                            avObject.getInt("price"), avObject.getInt("sales"));
                    ticketList.add(ticket);
                }
                initTicketRecyclerView();
            }
        });
    }

    public void initTicketRecyclerView() {
        TicketAdapter ticketAdapter = new TicketAdapter(ticketList);
        ticketRecyclerView.setAdapter(ticketAdapter);
        getTicketRemain();
    }

    public void getTicketRemain() {
        AVQuery<AVObject> query = new AVQuery<>("TicketTimeTable");
        query.whereEqualTo("projectName", projectName);
        query.whereEqualTo("date", changeToSymbolDate(time));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                Log.i("test4", String.valueOf(avObjects.size()));
                int i = 0;
                for (AVObject avObject : avObjects) {
                    for (Ticket ticket : ticketList) {
                        if (ticket.ticketType.equals(avObject.getString("ticket"))) {

                            ticketList.get(i).setRemain(avObject.getInt("remain"));
                            Log.i("test2", ticketList.get(i).ticketType + " " + ticketList.get(i).remain);
                        }
                    }
                    i++;
                }

                getDateInformation();
            }
        });
    }
    public void getDateInformation() {
        AVQuery<AVObject> query = new AVQuery<>("TimeTable");
        query.orderByAscending("date");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                for (AVObject avObject : avObjects) {
                    Date date = new Date(avObject.getString("date"), avObject.getInt("week"));
                    dateList.add(date);
                }
                initTimePanel();
            }
        });
    }
    public void initTimePanel() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        monthRecyclerView.setLayoutManager(linearLayoutManager);
        monthList = transToMonth(dateList);
        Log.i("monthList", String.valueOf(monthList.size()));
        MonthAdapter monthAdapter = new MonthAdapter(monthList);
        monthRecyclerView.setAdapter(monthAdapter);
        Log.i("Test", "setAdapterSuccess!");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeChosePanel:
                initTimePanel();
                timeChosePanel.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator = ofFloat(timeChosePanel, "translationY", screenHeight, 0);
                objectAnimator.setDuration(1000);
                objectAnimator.start();
                shelter.setVisibility(View.VISIBLE);
                break;
            case R.id.shelter:
            case R.id.cancel:
                timeChosePanel.setVisibility(View.INVISIBLE);
                shelter.setVisibility(View.INVISIBLE);
                timeChosePanelQuit.start();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {
        private List<Ticket> ticketList;

        private TicketAdapter(List<Ticket> ticketList) {
            this.ticketList = ticketList;
        }

        @Override
        public TicketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_ticket, parent, false);
            TicketAdapter.ViewHolder holder = new TicketAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final TicketAdapter.ViewHolder holder, int position) {
            Ticket ticket = ticketList.get(position);
            Log.i(String.valueOf(position), ticket.ticketType);
            holder.ticketType.setText(ticket.ticketType);
            holder.sales.setText("已售" + String.valueOf(ticket.sales));
            holder.price.setText(String.valueOf(ticket.price));
            holder.setProject(ticket.projectName);
            holder.order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userId.equals("tourist")) {
                        Intent loginIntent = new Intent(AmusementTicketActivity.this, LoginActivity.class);
                        startActivityForResult(loginIntent, 1);
                    } else {
                        orderAmusementType = holder.ticketType.getText().toString();
                        orderPrice = Integer.parseInt(holder.price.getText().toString());
                        orderDetail = topPanelChosenDate.getText().toString();
                        orderAVObject = new AVObject("Order");
                        orderAVObject.put("userId", userId);
                        orderAVObject.put("type", type);
                        orderAVObject.put("project", holder.project);
                        orderAVObject.put("status", "待支付");
                        orderAVObject.put("item", orderAmusementType);
                        orderAVObject.put("detail", orderDetail);
                        orderAVObject.put("price", orderPrice);
                        orderAVObject.put("count", 1);
                        orderAVObject.put("comment", false);
                        orderAVObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Intent orderIntent = new Intent(AmusementTicketActivity.this, CreateOrderActivity.class);
                                    orderIntent.putExtra("UserId", userId);
                                    orderIntent.putExtra("Type", type);
                                    orderIntent.putExtra("Project", holder.project);
                                    orderIntent.putExtra("Item", orderAmusementType);
                                    orderIntent.putExtra("Url", url);
                                    orderIntent.putExtra("Detail", orderDetail);
                                    orderIntent.putExtra("Count", 1);
                                    orderIntent.putExtra("Price", orderPrice);
                                    orderIntent.putExtra("OrderId", orderAVObject.getObjectId());
                                    startActivityForResult(orderIntent, 0);
                                }
                            }
                        });


                    }
                }
            });
            holder.payOnline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return ticketList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView ticketType;
            private TextView price;
            private TextView sales;
            private Button payOnline;
            private Button order;
            private String project;

            private ViewHolder(View view) {
                super(view);
                ticketType = view.findViewById(R.id.ticketType);
                price = view.findViewById(R.id.price);
                sales = view.findViewById(R.id.sales);
                payOnline = view.findViewById(R.id.payOnline);
                order = view.findViewById(R.id.onlineShop);
            }

            private void setProject(String project) {
                this.project = project;
            }
        }


    }
    private class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {
        private List<Week> weeksList;
        private int month;

        private WeekAdapter(List<Week> weeksList, int month) {
            this.weeksList = weeksList;
            this.month = month;
        }

        @NonNull
        @Override
        public WeekAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_week, parent, false);
            WeekAdapter.ViewHolder holder = new WeekAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final WeekAdapter.ViewHolder holder, int position) {
            Week week = weeksList.get(position);
            if (week.weekArray[0] == 0) {
                holder.Sun.setText("");
            } else {
                holder.Sun.setText(String.valueOf(week.weekArray[0]));
                holder.Sun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.SunDetail.setText("游玩");
                        holder.SunDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Sun.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
                    }
                });
            }
            if (week.weekArray[1] == 0) {
                holder.Mon.setText("");
            } else {
                holder.Mon.setText(String.valueOf(week.weekArray[1]));
                holder.Mon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.MonDetail.setText("游玩");
                        holder.MonDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Mon.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
                    }
                });
            }
            if (week.weekArray[2] == 0) {
                holder.Tue.setText("");
            } else {
                holder.Tue.setText(String.valueOf(week.weekArray[2]));
                holder.Tue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.TueDetail.setText("游玩");
                        holder.TueDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Tue.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
                    }
                });
            }
            if (week.weekArray[3] == 0) {
                holder.Wed.setText("");
            } else {
                holder.Wed.setText(String.valueOf(week.weekArray[3]));
                holder.Wed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.WedDetail.setText("游玩");
                        holder.WedDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Wed.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
                    }
                });
            }
            if (week.weekArray[4] == 0) {
                holder.Thu.setText("");
            } else {
                holder.Thu.setText(String.valueOf(week.weekArray[4]));
                holder.Thu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.ThuDetail.setText("游玩");
                        holder.ThuDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Thu.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
                    }
                });
            }
            if (week.weekArray[5] == 0) {
                holder.Fri.setText("");
            } else {
                holder.Fri.setText(String.valueOf(week.weekArray[5]));
                holder.Fri.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.FriDetail.setText("游玩");
                        holder.FriDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Fri.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
                    }
                });
            }
            if (week.weekArray[6] == 0) {
                holder.Sat.setText("");
            } else {
                holder.Sat.setText(String.valueOf(week.weekArray[6]));
                holder.Sat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setBackground(getResources().getDrawable(R.drawable.shape_days));
                        holder.SatDetail.setText("游玩");
                        holder.SatDetail.setVisibility(View.VISIBLE);
                        String timeMonth = String.valueOf(month);
                        if (month < 10) {
                            timeMonth = '0' + timeMonth;
                        }
                        String timeDay = holder.Sat.getText().toString();
                        if (Integer.parseInt(timeDay) < 10) {
                            timeDay = '0' + timeDay;
                        }
                        time = "2019年" + timeMonth + "月" + timeDay + "日";
                        topPanelChosenDate.setText(time);
                        Log.i("Time", time);
                        shelter.setVisibility(View.INVISIBLE);
                        timeChosePanelQuit.start();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return weeksList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView Sun;
            private TextView Mon;
            private TextView Tue;
            private TextView Wed;
            private TextView Thu;
            private TextView Fri;
            private TextView Sat;
            private TextView SunDetail;
            private TextView MonDetail;
            private TextView TueDetail;
            private TextView WedDetail;
            private TextView ThuDetail;
            private TextView FriDetail;
            private TextView SatDetail;

            private ViewHolder(View view) {
                super(view);
                Sun = view.findViewById(R.id.Sun);
                Mon = view.findViewById(R.id.Mon);
                Tue = view.findViewById(R.id.Tue);
                Wed = view.findViewById(R.id.Wed);
                Thu = view.findViewById(R.id.Thu);
                Fri = view.findViewById(R.id.Fri);
                Sat = view.findViewById(R.id.Sat);
                SunDetail = view.findViewById(R.id.Sun_detail);
                MonDetail = view.findViewById(R.id.Mon_detail);
                TueDetail = view.findViewById(R.id.Tue_detail);
                WedDetail = view.findViewById(R.id.Wed_detail);
                ThuDetail = view.findViewById(R.id.Thu_detail);
                FriDetail = view.findViewById(R.id.Fri_detail);
                SatDetail = view.findViewById(R.id.Sat_detail);
            }
        }

    }

    private class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {
        private List<Month> monthList;

        private MonthAdapter(List<Month> monthList) {
            this.monthList = monthList;
        }


        @NonNull
        @Override
        public MonthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_months, parent, false);
            MonthAdapter.ViewHolder holder = new MonthAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final MonthAdapter.ViewHolder holder, int position) {
            Month month = monthList.get(position);
            holder.month.setText(month.month);
            List<Week> weekList = transToWeek(month);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
            holder.weekRecyclerView.setLayoutManager(linearLayoutManager);
            WeekAdapter weekAdapter = new WeekAdapter(weekList, Integer.parseInt(month.month));
            holder.weekRecyclerView.setAdapter(weekAdapter);
        }

        @Override
        public int getItemCount() {
            return monthList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView month;
            private RecyclerView weekRecyclerView;

            private ViewHolder(View view) {
                super(view);
                month = view.findViewById(R.id.month);
                weekRecyclerView = view.findViewById(R.id.week);
            }
        }
    }

    public List<Month> transToMonth(List<Date> dateList) {
        List<Month> months = new ArrayList<>();
        int days[] = new int[6];
        int j = -1;
        for (int i = 0; i < dateList.size(); i++) {
            char[] dateChar = dateList.get(i).day.toCharArray();
            Date date = dateList.get(i);
            if (i == 0) {
                Log.i("dateExample", date.day);
            }
            //example:"2019/06/01"
            if (dateChar[8] == '0' & dateChar[9] == '1') {
//                Log.i("dateInformation", String.valueOf(dateChar[5]));
//                Log.i("dateInformation", String.valueOf(dateChar[6]));
//                Log.i("dateInformation", String.valueOf(dateChar[5] + String.valueOf(dateChar[6])));
                Month month = new Month(date.week, String.valueOf(dateChar[5] + String.valueOf(dateChar[6])), 0);
                Log.i("monthInformation", month.month + " " + String.valueOf(month.firstDayWeek));
                months.add(month);
                j++;
                days[j] = 1;
            } else {
                days[j]++;
            }
        }
        for (int k = 0; k < months.size(); k++) {
            months.get(k).setDays(days[k]);
        }
        return months;
    }

    public List<Week> transToWeek(Month month) {
        List<Week> weeks = new ArrayList<>();
        if (month.firstDayWeek == 7) {
            month.firstDayWeek = 0;
        }
        int[] firstWeekArray = new int[7];
        int k = 1;
        for (int j = 0; j < 7; j++) {
            if (j < month.firstDayWeek) {
                firstWeekArray[j] = 0;
            } else {
                firstWeekArray[j] = k;
                k++;
            }
        }
        Week firstWeek = new Week(firstWeekArray);
        weeks.add(firstWeek);

        int[] weekArray=new int[7];
        int l = 0;
        for (int i = 8 - month.firstDayWeek; i <= month.days; i++) {
            if ((i + month.firstDayWeek) % 7 == 1) {
                weekArray[0] = i;
//                Log.i("weekFirst", String.valueOf(weekArray[0]));
                l = 1;
            } else {
                weekArray[l] = i;
                if (l == 6) {
                    Week week = new Week(weekArray);
//                    Log.i("weekFinal", String.valueOf(weekArray[l]));
//                    Log.i("week.weekArray", String.valueOf(week.weekArray[l]));
                    weeks.add(week);
                    weekArray = new int[7];
                }
                l++;
            }
            if (i == month.days) {
                Week week = new Week(weekArray);
                weeks.add(week);
            }
        }
//        for (int i = 0; i < weeks.size(); i++) {
//            Log.i("weeks", String.valueOf(weeks.get(i).weekArray[0]));
//        }
        return weeks;
    }
    public String changeToSymbolDate(String date) {
        char[] dateChar = date.toCharArray();
        char[] dateChar2 = new char[dateChar.length - 1];
        for (int i = 0; i < dateChar.length; i++) {
            if (dateChar[i] < '0' || dateChar[i] > '9') {
                dateChar[i] = '/';
            }
        }
        for (int j = 0; j < dateChar2.length; j++) {
            dateChar2[j] = dateChar[j];
        }

        return String.valueOf(dateChar2);
    }

    private void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            toast.show();
        } else {
            toast.setText(msg);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    userId = data.getStringExtra("UserId");
                    showToast("登录成功！");
                }
                break;
        }
    }
}
