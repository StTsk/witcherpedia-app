package com.example.drhappy.witcherpedia;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class DescriptionActivity extends AppCompatActivity {
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link /FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	
	private DBHelper witcherDB;
	
	protected DBHelper getWitcherDB() {
		return witcherDB;
	}
	
	private ArrayList<String> names_alist;
	private String type;
	
	protected String getType() {
		return type;
	}
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	//private ViewPager mViewPager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description);
		
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		
		witcherDB = DBHelper.getInstance(this);
		
		Intent intent = getIntent();
		type = intent.getStringExtra("Type");
		getSupportActionBar().setTitle(type);
		
		names_alist = intent.getStringArrayListExtra("Adapter");
		String selected_name = intent.getStringExtra("Item_Selected");
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		ViewPager mViewPager = findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(names_alist.indexOf(selected_name));
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_description, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		//noinspection SimplifiableIfStatement
		if (id == android.R.id.home) {
			finish();
			return true;
		} else if (id == R.id.action_settings) {
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_LIST_ITEM_NAME = "list_item_name";
		
		public PlaceholderFragment() {
		}
		
		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(String itemName) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			
			Bundle args = new Bundle();
			args.putString(ARG_LIST_ITEM_NAME, itemName);
			fragment.setArguments(args);
			
			return fragment;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		                         Bundle savedInstanceState) {
			View rootView = null;
			
			String type = ((DescriptionActivity) getActivity()).getType();
			if (type.equals("Unit Description")) {
				rootView = inflater.inflate(R.layout.content_unit_description, container, false);
			} else if (type.equals("Territory Description")) {
				rootView = inflater.inflate(R.layout.content_territory_description, container, false);
			}
			
			setDescription(rootView, type, getArguments().getString(ARG_LIST_ITEM_NAME));
			
			return rootView;
		}
		
		
		void setDescription(View rootView, String type, String name) {
			ScrollView desc_scroll = rootView.findViewById(R.id.dscroll);
			desc_scroll.scrollTo(0, 0);
			
			if (type.equals("Unit Description")) {
				TextView utitle = rootView.findViewById(R.id.utitle);
				ImageView uicon = rootView.findViewById(R.id.uicon);
				TextView udesc = rootView.findViewById(R.id.udescription);
				
				try (Cursor resultSet = ((DescriptionActivity) getActivity()).getWitcherDB().getUnit(name)) {
					for (resultSet.moveToFirst(); !resultSet.isAfterLast(); resultSet.moveToNext()) {
						//String unitn = resultSet.getString(resultSet.getColumnIndex(DBHelper.COLUMN_UNITN));
						utitle.setText(name);
						
						String drawablen = resultSet.getString(resultSet.getColumnIndex("drawablen"));
						int resourceId = this.getResources().getIdentifier(drawablen, "drawable", getActivity().getPackageName());
						uicon.setImageResource(resourceId);
						
						String specialization = resultSet.getString(resultSet.getColumnIndex("specialization"));
						String category = resultSet.getString(resultSet.getColumnIndex("category"));
						udesc.setText(TextUtils.concat(setHtml("009688", "Category: "), specialization, ", ", category));
						
						String nationality = resultSet.getString(resultSet.getColumnIndex("nationality"));
						String classn = resultSet.getString(resultSet.getColumnIndex("class"));
						udesc.append(TextUtils.concat("\n", setHtml("009688", "Class: "), nationality, ", ", classn));
						
						int speed = resultSet.getInt(resultSet.getColumnIndex("speed"));
						int initiative = resultSet.getInt(resultSet.getColumnIndex("initiative"));
						
						if (category.equals("Support")) {
							int cost = resultSet.getInt(resultSet.getColumnIndex("cost"));
							String armylimit = resultSet.getString(resultSet.getColumnIndex("armylimit"));
							
							udesc.append(TextUtils.concat("\n", setHtml("009688", "Speed: "), String.valueOf(speed),
									"\n", setHtml("009688", "Initiative: "), String.valueOf(initiative),
									"\n", setHtml("009688", "Cost: "), String.valueOf(cost),
									"\n", setHtml("009688", "Limit: "), armylimit));
						} else {
							if (category.equals("Navy") || category.equals("Monster")) {
								int hitpoints = resultSet.getInt(resultSet.getColumnIndex("hitpoints"));
								udesc.append(TextUtils.concat("\n", setHtml("009688", "Hit Points: "), String.valueOf(hitpoints)));
							}
							
							String strength = resultSet.getString(resultSet.getColumnIndex("strength"));
							udesc.append(TextUtils.concat("\n", setHtml("009688", "Strength: "), strength));
							
							if (category.equals("Mage")) {
								int essence = resultSet.getInt(resultSet.getColumnIndex("essence"));
								udesc.append(TextUtils.concat("\n", setHtml("009688", "Essence: "), String.valueOf(essence)));
							}
							
							udesc.append(TextUtils.concat("\n", setHtml("009688", "Speed: "), String.valueOf(speed),
									"\n", setHtml("009688", "Initiative: "), String.valueOf(initiative)));
							
							if (!category.equals("Melee")) {
								int range = resultSet.getInt(resultSet.getColumnIndex("range"));
								udesc.append(TextUtils.concat("\n", setHtml("009688", "Range: "), String.valueOf(range)));
							}
							
							if (!category.equals("Monster")) {
								int cost = resultSet.getInt(resultSet.getColumnIndex("cost"));
								String armylimit = resultSet.getString(resultSet.getColumnIndex("armylimit"));
								udesc.append(TextUtils.concat("\n", setHtml("009688", "Cost: "), String.valueOf(cost),
										"\n", setHtml("009688", "Limit: "), armylimit));
							}
						}
						String abilities = resultSet.getString(resultSet.getColumnIndex("abilities"));
						udesc.append(TextUtils.concat("\n", setHtml("009688", "Abilities: "), setHtml("-1", abilities)));
					}
					
				}
			} else if (type.equals("Territory Description")) {
				ImageView ticon = rootView.findViewById(R.id.ticon);
				TextView ttitle = rootView.findViewById(R.id.ttitle);
				TextView tdesc = rootView.findViewById(R.id.tdescription);
				
				String territoryn = name.replaceFirst("<font color='#3A3A3A'>[0-9]+ </font>", "");
				try (Cursor resultSet = ((DescriptionActivity) getActivity()).getWitcherDB().getTerritory(territoryn)) {
					for (resultSet.moveToFirst(); !resultSet.isAfterLast(); resultSet.moveToNext()) {
						int num = resultSet.getInt(resultSet.getColumnIndex("num"));
						//String territoryn = resultSet.getString(resultSet.getColumnIndex("territoryn"));
						ttitle.setText(TextUtils.concat(setHtml("3A3A3A", String.valueOf(num)), " ", territoryn));
						
						String kingdom = resultSet.getString(resultSet.getColumnIndex("factionn"));
						tdesc.setText(TextUtils.concat(setHtml("009688", "Kingdom: "), kingdom));
						
						String category = resultSet.getString(resultSet.getColumnIndex("category"));
						tdesc.append(TextUtils.concat("\n", setHtml("009688", "Category: "), category));
						
						String region = resultSet.getString(resultSet.getColumnIndex("region"));
						tdesc.append(TextUtils.concat("\n", setHtml("009688", "Region: "), region));
						
						int income = resultSet.getInt(resultSet.getColumnIndex("income"));
						tdesc.append(TextUtils.concat("\n", setHtml("009688", "Income: "), String.valueOf(income)));
						
						if (category.equals("Fortified")) {
							String status = resultSet.getString(resultSet.getColumnIndex("status"));
							String defences = resultSet.getString(resultSet.getColumnIndex("defences"));
							
							tdesc.append(TextUtils.concat("\n", setHtml("009688", "Status: "), status,
									"\n", setHtml("009688", "Defences: "), setHtml("-1", defences)));
							
							
							int resourceId = getActivity().getResources().getIdentifier("ic_fortified_territory", "drawable", getActivity().getPackageName());
							ticon.setImageResource(resourceId);
						} else {
							int resourceId = getActivity().getResources().getIdentifier("ic_open_territory", "drawable", getActivity().getPackageName());
							ticon.setImageResource(resourceId);
						}
						
						String characteristics = resultSet.getString(resultSet.getColumnIndex("characteristics"));
						tdesc.append(TextUtils.concat("\n", setHtml("009688", "Characteristics: "), setHtml("-1", characteristics)));
					}
					
				}
				setTerritoryTitleCentered(ttitle, ttitle.getText().toString(), ticon);
			}
		}
		
		private Spanned setHtml(String color, String stat) {
			String ifcolor;
			if (color.equals("-1")) {
				ifcolor = "";
			} else {
				ifcolor = "color='#" + color + "'";
			}
			
			Spanned html;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				html = Html.fromHtml("<font " + ifcolor + ">" + stat + "</font>", Html.FROM_HTML_MODE_COMPACT);
			} else {
				html = Html.fromHtml("<font " + ifcolor + ">" + stat + "</font>");
			}
			
			return html;
		}
		
		private void setTerritoryTitleCentered(TextView ttitle, String text, ImageView ticon) {
			DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
			float screenWidthInDp = 1.0f * displayMetrics.widthPixels / displayMetrics.density;
			
			Rect bounds = new Rect();
			Paint textPaint = ttitle.getPaint();
			textPaint.getTextBounds(text, 0, text.length(), bounds);
			int textWidthInPixels = bounds.width();
			float textWidthInDp = 1.0f * textWidthInPixels / displayMetrics.density;
			
			float sum = textWidthInDp + 38f;
			float marginInDp = (screenWidthInDp - sum) / 2;
			float marginInPixels = marginInDp * displayMetrics.density;
			
			ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) ticon.getLayoutParams();
			params.leftMargin = (int) marginInPixels - 23;
			
			ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) ttitle.getLayoutParams();
			param.rightMargin = (int) marginInPixels - 10;
		}
	}
	
	/**
	 * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
		
		SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			//System.out.println("Pos: "+position);
			//System.out.println("Unit in that pos: "+names_alist.get(position));
			
			return PlaceholderFragment.newInstance(names_alist.get(position));
		}
		
		@Override
		public int getCount() {
			// Show 3 total pages.
			return names_alist.size();
		}
	}
	
	
}