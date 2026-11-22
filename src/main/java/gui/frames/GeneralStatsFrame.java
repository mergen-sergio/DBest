package gui.frames;

import java.util.StringJoiner;

import javax.swing.JDialog;
import javax.swing.JTextPane;

import controllers.ConstantController;
import engine.info.Parameters;
import files.FileUtils;
import ibd.query.QueryStats;
import javax.swing.ImageIcon;

public class GeneralStatsFrame extends JDialog {

    public GeneralStatsFrame() {
        try {
            this.setIconImage(new ImageIcon(String.valueOf(FileUtils.getDBestLogo())).getImage());
        }catch (Exception ignored){
        }
        JTextPane textPane = new JTextPane();

        this.add(textPane);

        StringJoiner textPaneText = new StringJoiner("\n");

        String queryStats = String.format("\n%s:", ConstantController.getString("dataframe.query"));
        String primaryKeySearchStat = String.format("%s = %s", ConstantController.getString("PK_SEARCH"), QueryStats.PK_SEARCH.sum());
        String sortedTuplesStat = String.format("%s = %s", ConstantController.getString("SORT_TUPLES"), QueryStats.SORT_TUPLES.sum());
        String filterComparisonStat = String.format("%s = %s", ConstantController.getString("COMPARE_FILTER"), QueryStats.COMPARE_FILTER.sum());
        String recordsReadStat = String.format("%s = %s", ConstantController.getString("RECORDS_READ"), Parameters.RECORDS_READ.sum());
        String nextCallsStat = String.format("%s = %s", ConstantController.getString("NEXT_CALLS"), QueryStats.NEXT_CALLS.sum());
        String distinctTuplesComparisonStat = String.format("%s = %s", ConstantController.getString("COMPARE_DISTINCT_TUPLE"), QueryStats.COMPARE_DISTINCT_TUPLE.sum());

        textPaneText.add(queryStats);
        textPaneText.add(primaryKeySearchStat);
        textPaneText.add(sortedTuplesStat);
        textPaneText.add(filterComparisonStat);
        textPaneText.add(recordsReadStat);
        textPaneText.add(nextCallsStat);
        textPaneText.add(distinctTuplesComparisonStat);
        textPaneText.add("");

        String diskStats = String.format("\n%s:", ConstantController.getString("dataframe.disk"));
        String ioSeekWriteTimeStat = String.format("%s = %fms", ConstantController.getString("IO_SEEK_WRITE_TIME"), (Parameters.IO_SEEK_WRITE_TIME.sum() ) / 1000000f);
        String ioWriteTimeStat = String.format("%s = %fms", ConstantController.getString("IO_WRITE_TIME"), (Parameters.IO_SEEK_WRITE_TIME.sum() ) / 1000000f);
        String ioSeekReadTimeStat = String.format("%s = %fms", ConstantController.getString("IO_SEEK_READ_TIME"), (Parameters.IO_SEEK_READ_TIME.sum() ) / 1000000f);
        String ioReadTimeStat = String.format("%s = %fms", ConstantController.getString("IO_READ_TIME"), (Parameters.IO_READ_TIME.sum() ) / 1000000f);
        String ioSyncTimeStat = String.format("%s = %fms", ConstantController.getString("IO_SYNC_TIME"), (Parameters.IO_SYNC_TIME.sum() ) / 1000000f);
        String ioTimeStat = String.format("%s = %fms", ConstantController.getString("IO_TOTAL_TIME"), (Parameters.IO_SYNC_TIME.sum() + Parameters.IO_SEEK_WRITE_TIME.sum() + Parameters.IO_READ_TIME.sum() + Parameters.IO_SEEK_READ_TIME.sum() + Parameters.IO_WRITE_TIME.sum() ) / 1000000f);
        String blocksLoadedStat = String.format("%s = %d", ConstantController.getString("dataframe.disk.loaded"), Parameters.BLOCKS_LOADED.sum());
        String blocksAccessedStat = String.format("%s = %d", ConstantController.getString("dataframe.disk.accessed"), Parameters.BLOCKS_ACCESSED.sum());
        String blocksSavedStat = String.format("%s = %d", ConstantController.getString("dataframe.disk.saved"), Parameters.BLOCKS_SAVED.sum());

        textPaneText.add(diskStats);
        textPaneText.add(ioSeekWriteTimeStat);
        textPaneText.add(ioWriteTimeStat);
        textPaneText.add(ioSeekReadTimeStat);
        textPaneText.add(ioReadTimeStat);
        textPaneText.add(ioSyncTimeStat);
        textPaneText.add(ioTimeStat);
        textPaneText.add(blocksLoadedStat);
        textPaneText.add(blocksAccessedStat);
        textPaneText.add(blocksSavedStat);

        textPane.setText(textPaneText.toString());
        textPane.setEditable(false);

        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
