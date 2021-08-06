package ir.yaddasht.yaddasht.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import ir.yaddasht.yaddasht.R;

import ir.yaddasht.yaddasht.model.roomdb.Note;
import ir.yaddasht.yaddasht.util.FormatHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ir.huri.jcal.JalaliCalendar;


public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {//implements Filterable  {////implements Filterable is for search
//    private List<Note> notesListFull;

    private String colorTable[] = {"", "#222222", "#5A2B2B", "#604A1C", "#635C1C",
            "#355922", "#14504A", "#2E555D", "#1C3A5D", "#422B5D", "#5A2244"};

    private OnItemClickListener listener;
    private OnItemLongClickListener listenerLong;


    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getContent().equals(newItem.getContent()) &&
                    oldItem.getColor() == newItem.getColor() &&
                    oldItem.getTimeAddEdit() == newItem.getTimeAddEdit()
                    ;
        }
    };

    @NonNull
    @Override
    public NoteAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteHolder holder, int position) {

//        holder.alertCardView.setVisibility(View.GONE);
        holder.holderLayout.setVisibility(View.GONE);
        holder.checkedLayout.setVisibility(View.GONE);

        holder.marginBelowTitle.setVisibility(View.GONE);

        Note currentNote = getItem(position);


        String[] contentArray = currentNote.getContent().split("\n");
        String[] checkArray = currentNote.getChecklistTick().split("\n");
        int checkedItems = 0;
        for (int i = 0; i < checkArray.length; i++) {
            if (checkArray[i].equals("true")) {
                checkedItems++;
            }
        }
        ///
//        if(checkedItems+1==checkArray.length){
//            holder.holderLayout.setVisibility(View.GONE);
//        }
        ///
        int uncheckedItems = 0;
        uncheckedItems = checkArray.length - checkedItems;
        //if title is empty
        if (currentNote.getTitle().isEmpty()) {
            holder.textTitle.setVisibility(View.GONE);
        } else { //title not empty
            holder.textTitle.setVisibility(View.VISIBLE);///
            holder.textTitle.setText(currentNote.getTitle());
            if(currentNote.getContent().isEmpty()){
                holder.marginBelowTitle.setVisibility(View.VISIBLE);
            }
        }
        //if content is empty
        if (currentNote.getContent().isEmpty()) {
            holder.textContent.setVisibility(View.GONE);
        } else { // content not empty
            holder.textContent.setVisibility(View.VISIBLE);///
            // if note is not checklist
            if (!currentNote.isCheckList()) {
                holder.holderLayout.setVisibility(View.GONE);
                if (contentArray.length < 5) {
                    holder.textContent.setText(currentNote.getContent());
                } else {
//                    holder.holderLayout.setVisibility(View.VISIBLE);///
                    String str = "";
                    for (int i = 0; i < 5; i++) {
                        str = str + contentArray[i];
                        if (i != 4) {
                            str = str + "\n";
                        } else {
                            str = str + " ... ";
                        }
                    }
                    holder.textContent.setText(str);

                }
            } else { //if note is checklist
                ////////////add check list
                holder.textContent.setVisibility(View.GONE);
                holder.holderLayout.setVisibility(View.VISIBLE);

                holder.holderLayout.removeAllViews();
                int count = 0;
                for (int i = 0; i < checkArray.length; i++) {
                    if (count == 5) {
                        break;
                    }
                    if (i == 0 || checkArray[i].equals("true")) {
                        continue;
                    }
                    if(i<contentArray.length){
                        if (count == 4 && uncheckedItems > 6) {
                            insertCheckList(holder.holderLayout, contentArray[i], true, i, holder.holderLayout.getContext());
                            insertMoreCheckList(holder.holderLayout,i+1,holder.holderLayout.getContext());

                            count++;
//                        break;
                        } else {
                            insertCheckList(holder.holderLayout, contentArray[i], true, i, holder.holderLayout.getContext());
                            count++;
                        }
                    }else {
                        if (count == 4 && uncheckedItems > 6) {
                            insertCheckList(holder.holderLayout, "", true, i, holder.holderLayout.getContext());
                            insertMoreCheckList(holder.holderLayout,i+1,holder.holderLayout.getContext());
                            count++;
//                        break;
                        } else {
                            insertCheckList(holder.holderLayout, "", true, i, holder.holderLayout.getContext());
                            count++;
                        }
                    }


                }
                if (checkedItems > 0) {

                    holder.checkedLayout.setVisibility(View.VISIBLE);
                    holder.checkedTextView.setText(FormatHelper.toPersianNumber(checkedItems + ""));
                } else {
                    holder.checkedLayout.setVisibility(View.GONE);
                }

                //// if all checkLists are checked
                if(checkedItems+1==checkArray.length){
                    holder.holderLayout.setVisibility(View.GONE);
                    holder.marginBelowTitle.setVisibility(View.VISIBLE);
                }

            }
        }

        if (currentNote.getTimeReminder() > 0) {
            Calendar cl = Calendar.getInstance();
            cl.setTimeInMillis(currentNote.getTimeReminder());
            holder.alertTextView.setText(customFormatTimeDate(cl));
            holder.alertCardView.setVisibility(View.VISIBLE);
            holder.alertCardChild.setCardBackgroundColor(Color.parseColor(colorTable[currentNote.getColor()]));
            //////////////////////alert cardview passed time////////////////
            long timeN = new Date().getTime();
            if(currentNote.getTimeReminder()< new Date().getTime()){
                holder.alertTextView.setTextColor(Color.GRAY);
                holder.alertTextView.setPaintFlags(holder.alertTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.imageView.setImageResource(R.drawable.ic_alarm_gray);
                holder.alertCardView.setCardBackgroundColor(Color.GRAY);
            } else {
                holder.alertTextView.setTextColor(Color.WHITE);
                holder.alertTextView.setPaintFlags(holder.alertTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.imageView.setImageResource(R.drawable.ic_alarm);
                holder.alertCardView.setCardBackgroundColor(Color.WHITE);
            }
        } else {
            holder.alertCardView.setVisibility(View.GONE);
        }

        holder.crdView2.setCardBackgroundColor(Color.parseColor(colorTable[currentNote.getColor()]));
    }


    public Note getNoteAt(int position) {
        return getItem(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private TextView textContent, textTitle;
        private CardView crdView2, crdView1;
        private LinearLayout holderLayout, checkedLayout;
        private TextView checkedTextView;
        private CardView alertCardView;
        private TextView alertTextView;
        private View marginBelowTitle;
        private CardView alertCardChild;

        private ImageView imageView;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            crdView2 = itemView.findViewById(R.id.crd_view2);
            crdView1 = itemView.findViewById(R.id.crd_view1);
            textContent = itemView.findViewById(R.id.txt_content);
            textTitle = itemView.findViewById(R.id.txt_title);
            holderLayout = itemView.findViewById(R.id.holder_ll);
            checkedLayout = itemView.findViewById(R.id.more_checked_item_layout);
            checkedTextView = itemView.findViewById(R.id.checked_item_textview);
            alertCardView = itemView.findViewById(R.id.alert_view_root);
            alertTextView = itemView.findViewById(R.id.alert_view_text);
            marginBelowTitle = itemView.findViewById(R.id.margin_below_title_textview);
            alertCardChild = itemView.findViewById(R.id.alert_view);

            imageView = itemView.findViewById(R.id.alert_view_image);

            itemView.setOnCreateContextMenuListener(this);

            ////////////onClick////////////////
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });

//            ///////////onLongClick////////////////
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    int position = getAdapterPosition();
//                    if(listenerLong != null &&position != RecyclerView.NO_POSITION){
//                        listenerLong.onItemLongClick(getItem(position));
//                        return true;
//                    }
//                    return false;
//                }
//            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if(v.getContext().toString().contains("MainActivity")){
                if (getNoteAt(this.getAbsoluteAdapterPosition()).isPinned()) {
                    menu.add(this.getAbsoluteAdapterPosition(), 221, 0, "حذف کردن");
                    menu.add(this.getAbsoluteAdapterPosition(), 222, 0, "در آوردن از سنجاق ");

                } else {
                    menu.add(this.getAbsoluteAdapterPosition(), 121, 0, "حذف کردن");
                    menu.add(this.getAbsoluteAdapterPosition(), 122, 0, "سنجاق کردن");

                }
            }

        }
    }

    public Note getNoteAtt(int position) {
        return getItem(position);
    }

    ////////click listener/////////////
    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    ////////click listener/////////////
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    ///long click
    public interface OnItemLongClickListener {
        void onItemLongClick(Note note);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listenerLong = listener;
    }


    //////////////////////////// implemented with other methode! ///////////////
    ///////////////////////////////Search///////////////////////////
//    @Override
//    public Filter getFilter() {
//        return notesFilter;
//    }
//    /////////////does work in background thread//////////
//    private Filter notesFilter = new Filter() {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            List<Note> filteredList = new ArrayList<>();
//
//            if (constraint == null || constraint.length() == 0){
//                filteredList.addAll(notesListFull);
//            } else {
//                String filterPattern = constraint.toString().toLowerCase().trim();
//
//                for (Note item : notesListFull){
//                    if(item.getContent().toLowerCase().contains(filterPattern)){
//                        filteredList.add(item);
//                    }
//                }
//            }
//
//            FilterResults results = new FilterResults();
//            results.values = filteredList;
//
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            notes.clear();
//            notes.addAll((List) results.values);
//            notifyDataSetChanged();
//        }
//    };
    //////////////////////////////////////////



    private void insertCheckList(LinearLayout root, String line, boolean ticked, int id, Context context) {

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        TextView e = new TextView(context);
        ImageView img = new ImageView(context);

        e.setId(2000 + id);
        e.setText(line);
        e.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        3f
                )
        );
        e.setTextColor(Color.WHITE);

        img.setId(3000 + id);
        img.setImageResource(R.drawable.ic_check_box_outline_blank_opacity50);
        img.setBackgroundColor(Color.TRANSPARENT);

        img.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        LinearLayout l = new LinearLayout(context);
        l.setId(4000 + id);
        l.setGravity(Gravity.CENTER);
        l.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        l.addView(img);
        l.addView(e);
        root.addView(l, p);
    }

    private void insertMoreCheckList(LinearLayout root,int id, Context context) {

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        TextView e = new TextView(context);

        e.setId(2000 + id);
        e.setText("  ...  ");
        e.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        3f
                )
        );
        e.setTextColor(Color.WHITE);



        LinearLayout l = new LinearLayout(context);
        l.setId(4000 + id);
        l.setGravity(Gravity.CENTER);
        l.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

//        l.addView(img);
        l.addView(e);
        root.addView(l, p);
    }


    String customFormatTimeDate(Calendar alert) {
        ///////////////////Miladi////////////////
//        Calendar tNow = Calendar.getInstance();
//        String out;
//        if (alert.get(Calendar.DAY_OF_YEAR) == tNow.get(Calendar.DAY_OF_YEAR)) {
//            out = "Today, ";
//        } else if (alert.get(Calendar.DAY_OF_YEAR) == tNow.get(Calendar.DAY_OF_YEAR) + 1) {
//            out = "Tomorrow, ";
//        } else {
//            out = alert.get(Calendar.DAY_OF_MONTH) + " " + alert.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
//            if (alert.get(Calendar.YEAR) != tNow.get(Calendar.YEAR)) {
//                out = out + " " + alert.get(Calendar.YEAR);
//            }
//            out = out + ", ";
//        }
//        Date tt = alert.getTime();
//        SimpleDateFormat cFormat = new SimpleDateFormat("HH:mm");
//        out = out + cFormat.format(tt);
//        return out;
        //////////////////Miladi/////////////////////
        /////////////////////////////////
        Calendar tNow = Calendar.getInstance();
        JalaliCalendar alertJalali = new JalaliCalendar();
        JalaliCalendar tNowJalali = new JalaliCalendar();
        alertJalali.fromGregorian(new GregorianCalendar(alert.get(Calendar.YEAR),
                alert.get(Calendar.MONTH), alert.get(Calendar.DAY_OF_MONTH)));
        tNowJalali.fromGregorian(new GregorianCalendar(tNow.get(Calendar.YEAR),
                tNow.get(Calendar.MONTH), tNow.get(Calendar.DAY_OF_MONTH)));
        String out;
        if (alertJalali.getYear() == tNowJalali.getYear() && alert.get(Calendar.DAY_OF_YEAR) == tNow.get(Calendar.DAY_OF_YEAR)) {
            out = "امروز، ";
        } else if (alertJalali.getYear() == tNowJalali.getYear() && alert.get(Calendar.DAY_OF_YEAR) == tNow.get(Calendar.DAY_OF_YEAR) + 1) {
            out = "فردا، ";
        } else {
            out = alertJalali.getDay() + " " + alertJalali.getMonthString();
            if (alertJalali.getYear() != tNowJalali.getYear()) {
                out = out + " " + alertJalali.getYear();
            }
            out = out + "، ";

        }
        Date tt = alert.getTime();
        SimpleDateFormat cFormat = new SimpleDateFormat("HH:mm");
        out = out + cFormat.format(tt);
        out = FormatHelper.toPersianNumber(out);
        return out;
    }

}
