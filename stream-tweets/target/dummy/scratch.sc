import org.apache.spark.sql.functions.{col, current_date, date_sub, row_number, to_date}

date_sub(current_date(),7)