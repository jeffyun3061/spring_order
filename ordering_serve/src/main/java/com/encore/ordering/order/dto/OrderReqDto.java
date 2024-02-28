package com.encore.ordering.order.dto;

import lombok.Data;

//@Data
//public class OrderReqDto {
//    private List<Long> itemIds;
//    private List<Long> counts;
//}
//예시데이터
/*
{
    "itemIds" : [1,2], "counts" : [10,20]
}
*/

@Data
public class OrderReqDto {
        private Long itemId;
        private int count;

}
//예시데이터
/*
"orderReqItemDtos": [
{"itemId" : 1, "count" : 10},
{"itemId" : 2, "count" : 20}
]
 */
