package fmt.febulous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class Dashboard extends AppCompatActivity {

    @SuppressWarnings("ResourceType")
    ListView DashboardList;

    String[] DashboardTitles;
    String[] DashboardDescriptions;
    TypedArray DashboardIcons;

    ArrayList<MenuListItem> DashboardItemList;
    Dashboard.MenuListAdapter DashboardListAdapter;

    String actionbar_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        actionbar_title="\tDASHBOARD";
        setTitle(actionbar_title);

        DashboardTitles = getResources().getStringArray(R.array.dashboard_titles);
        DashboardDescriptions = getResources().getStringArray(R.array.dashboard_descriptions);
        DashboardIcons = getResources().obtainTypedArray(R.array.dashboard_icons);
        DashboardList = (ListView) findViewById(R.id.dashboard_list);
        DashboardItemList = new ArrayList<>();

        DashboardItemList.add(new MenuListItem(DashboardTitles[0], DashboardDescriptions[0],
                ContextCompat.getColor(this, R.color.colorGK), DashboardIcons.getResourceId(0, -1)));

        DashboardItemList.add(new MenuListItem(DashboardTitles[1], DashboardDescriptions[1],
                ContextCompat.getColor(this, R.color.colorQnA),DashboardIcons.getResourceId(1, -1)));

        DashboardItemList.add(new MenuListItem(DashboardTitles[2], DashboardDescriptions[2],
                ContextCompat.getColor(this, R.color.colorStudyMaterials),DashboardIcons.getResourceId(2, -1)));

        DashboardItemList.add(new MenuListItem(DashboardTitles[3], DashboardDescriptions[3],
                ContextCompat.getColor(this, R.color.colorTeach),DashboardIcons.getResourceId(3, -1)));

        DashboardItemList.add(new MenuListItem(DashboardTitles[4], DashboardDescriptions[4],
                ContextCompat.getColor(this, R.color.colorIdeas),DashboardIcons.getResourceId(4, -1)));

        DashboardItemList.add(new MenuListItem(DashboardTitles[5], DashboardDescriptions[5],
                ContextCompat.getColor(this, R.color.colorEvents),DashboardIcons.getResourceId(5, -1)));

        DashboardIcons.recycle();

        DashboardListAdapter = new MenuListAdapter(this, DashboardItemList);
        DashboardList.setAdapter(DashboardListAdapter);
        DashboardList.setOnItemClickListener(new SlideMenuClickListener());

    }


    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            displayView(position);
        }
    }


    private void displayView(int position) {

        switch (position) {

            case 0:

                Intent gk = new Intent(this, PostTemplate.class);
                gk.putExtra("type", "GK");
                startActivity(gk);

                break;

            case 1:

                Intent qna = new Intent(this, PostTemplate.class);
                qna.putExtra("type", "QNA");
                startActivity(qna);

                break;

            case 2:

                Intent sm = new Intent(this, PostTemplate.class);
                sm.putExtra("type", "Materials");
                startActivity(sm);

                break;

            case 3:

                Intent teach = new Intent(this, PostTemplate.class);
                teach.putExtra("type", "Teach");
                startActivity(teach);

                break;

            case 4:

                Intent ideas = new Intent(this, PostTemplate.class);
                ideas.putExtra("type", "Ideas");
                startActivity(ideas);

                break;

            case 5:

                Intent events = new Intent(this, PostTemplate.class);
                events.putExtra("type", "Events");
                startActivity(events);

                break;

            default:
                break;
        }
    }



    private class MenuListAdapter extends BaseAdapter {

        private Context context;

        private ArrayList<Dashboard.MenuListItem> menuListItems;

        public MenuListAdapter(Context context, ArrayList<Dashboard.MenuListItem> menuListItems){

            this.context = context;
            this.menuListItems = menuListItems;

        }

        @Override
        public int getCount() {
            return menuListItems.size();
        }

        @Override
        public Object getItem(int position) {
            return menuListItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                LayoutInflater mInflater = (LayoutInflater)
                        context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.activity_dashboard_list_item, parent, false);

            }

            ImageView imgIcon = (ImageView) convertView.findViewById(R.id.db_list_item_icon);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.db_list_item_title);
            TextView txtDescription = (TextView) convertView.findViewById(R.id.db_list_item_description);
            LinearLayout relativeLayout = (LinearLayout) convertView.findViewById(R.id.db_list_item_LL);

            imgIcon.setImageResource(menuListItems.get(position).getIcon());

            txtTitle.setText(menuListItems.get(position).getTitle());

            txtTitle.setTextColor(menuListItems.get(position).getBGColor());

            txtDescription.setText(menuListItems.get(position).getDescription());

            relativeLayout.setBackgroundColor(menuListItems.get(position).getBGColor());

            return convertView;
        }
    }


    private class MenuListItem {

        private String title;
        private String description;
        private int bgcolor;
        private int icon;

        public MenuListItem(String title, String description, int bgcolor, int icon){
            this.title = title;
            this.description = description;
            this.bgcolor = bgcolor;
            this.icon = icon;
        }

        public String getTitle(){
            return this.title;
        }

        public String getDescription(){
            return this.description;
        }

        public int getBGColor(){
            return this.bgcolor;
        }

        public int getIcon(){
            return this.icon;
        }

        public void setTitle(String title){
            this.title = title;
        }

        public void setDescription(String description){
            this.description = description;
        }

        public void setIcon(int icon){
            this.icon = icon;
        }

    }

}