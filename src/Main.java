import model.CalculateResult;
import model.DownloadResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Calendar start = Calendar.getInstance();

        int finishCounter = 0;

        ExecutorService downloadService = Executors.newFixedThreadPool(10) ;
        ExecutorService calculateService = Executors.newFixedThreadPool(10) ;

        List<Future<DownloadResult>> downloadList = new ArrayList<>();
        List<Future<CalculateResult>> calculateList = new ArrayList<>();
        //23sec
        for(int i = 0; i< 100; i++){
            Download d = new Download(i);
            Future downloadResultFuture = downloadService.submit(d);
            downloadList.add(downloadResultFuture);
        }

        downloadService.shutdown();

//        for(int i = 0; i< 3000; i++){
//            Download d = new Download(i);
//            Calculate c = new Calculate(d.downloadNext());
//            Future calcResultFuture = calculateService.submit(c);
//            calculateList.add(calcResultFuture);
//        }
        for(Future<DownloadResult> d : downloadList) {
            calculateList.add(calculateService.submit(new Calculate(d.get())));
        }

        for(Future<CalculateResult> calculateResultFuture : calculateList){
            if(calculateResultFuture.get().found){
                finishCounter++;
            }
        }
        calculateService.shutdown();

        System.out.println("Total success checks: " + finishCounter);

        Calendar stop = Calendar.getInstance();

        System.out.println("Total time: " + (stop.getTimeInMillis() - start.getTimeInMillis()) + " ms");

    }
}
