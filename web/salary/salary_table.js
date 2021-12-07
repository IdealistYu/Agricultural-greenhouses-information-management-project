jQuery(document).ready(function() {
    console.log("进入salary_table.js");
    initDatatable();
});

let url="../salaryModule_file_servlet_action";

let salaryId;

function initDatatable(isOrdered){
    $('.datatable').dataTable( {
        "destroy":true,
        "paging":true,
        "searching":false,
        "oLanguage": {
            "aria": {
                "sortAscending": ": activate to sort column ascending",
                "sortDescending": ": activate to sort column descending"
            },
            "sProcessing":   "处理中...",
            "sLengthMenu":   "_MENU_ 记录/页",
            "sZeroRecords":  "没有匹配的记录",
            "sInfo":         "显示第 _START_ 至 _END_ 项记录，共 _TOTAL_ 项",
            "sInfoEmpty":    "显示第 0 至 0 项记录，共 0 项",
            "sInfoFiltered": "(由 _MAX_ 项记录过滤)",
            "sInfoPostFix":  "",
            "sSearch":       "过滤:",
            "oPaginate": {
                "sFirst":    "首页",
                "sPrevious": "上页",
                "sNext":     "下页",
                "sLast":     "末页"
            }
        },
        "aoColumns": [{"mRender": function(data, type, full) {
                sReturn = '<input type="checkbox" class="checkboxes" value="'+full.salary_id+'"/>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.employee_id+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.employee_name+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.employee_duty+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.salary_number+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.salary_month+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.salary_remark+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.salary_datetime+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+"<a onclick=\"submitDeleteRecord("+full.salary_id+")\">删除</a>"+'</div>';
                sReturn += '<div>'+"<a onclick=\"onModifyRecord("+full.salary_id+")\">修改</a>"+'</div>';
                return sReturn;
            },"orderable": false
        }],


        "aLengthMenu": [[5,10,15,20,25,40,50,-1],[5,10,15,20,25,40,50,"所有记录"]],
        "fnDrawCallback": function(){$(".checkboxes").uniform();$(".group-checkable").uniform();},
        //"sAjaxSource": "get_record.jsp"
        "sAjaxSource": isOrdered?"../salaryModule_file_servlet_action?Action=showSalaryTable&isOrdered=true":"../salaryModule_file_servlet_action?Action=showSalaryTable&isOrdered=false"
    });
    $('.datatable').find('.group-checkable').change(function () {
        var set = jQuery(this).attr("data-set");
        var checked = jQuery(this).is(":checked");
        jQuery(set).each(function () {
            if (checked) {
                $(this).attr("checked", true);
                $(this).parents('tr').addClass("active");
            } else {
                $(this).attr("checked", false);
                $(this).parents('tr').removeClass("active");
            }
        });
        jQuery.uniform.update(set);
    });
    $('.datatable').on('change', 'tbody tr .checkboxes', function () {
        $(this).parents('tr').toggleClass("active");
    });
}


function onAddRecord(){
    $("#AddSalaryRecord").show();
}

function onQueryRecord(){
    $("#QuerySalaryRecord").show();
}

function onPrintRecord(){
    alert("前往打印");
    window.parent.location.href="print_salary_table.html";
}

function onOrderRecord(){
    // $("#record_list tbody").remove();
    initDatatable(true);

    alert("按工资降序");
}

function onStatisticRecord(){
    window.location.href="statistic_table.html";
}

function onExportRecord(){
    let url="../salaryModule_file_servlet_action";
    let message={};
    message.Action="exportSalaryRecord";
    $.post(url,message,function (json) {
            if (json.ok === 200) {
                alert(JSON.stringify(json));
                alert("导出成功！");
                $("#ExportSalaryRecord #download_url").attr("href",json.download_url);
                $("#ExportSalaryRecord").show();
            } else {
                // 否则登录失败
                alert("导出失败！");
            }
        }
    )
}

function onModifyRecord(salary_id){
    salaryId=salary_id;
    $("#ModifySalaryRecord").show();
}

function submitAddRecord(){
    let url="../salaryModule_file_servlet_action";
    let message={};
    message.Action="addSalaryRecord";
    message.employee_id=$("#AddSalaryRecord #employee_id").val();
    message.employee_name=$("#AddSalaryRecord #employee_name").val();
    message.employee_duty=$("#AddSalaryRecord #employee_duty").val();
    message.salary_number=$("#AddSalaryRecord #salary_number").val();
    message.salary_remark=$("#AddSalaryRecord #salary_remark").val();
    message.salary_month=$("#AddSalaryRecord #salary_month").val();
    $.post(url,message,function (json) {
            if (json.ok === 200) {
                window.location.href="check_table.html";
                alert("添加成功！");
            } else {
                // 否则登录失败
                alert("添加失败！");
            }
        }
    )
    $("#AddSalaryRecord").hide();
}

function submitDeleteRecord(salary_id){
    let url="../salaryModule_file_servlet_action";
    let message={};
    message.salary_id=salary_id;
    message.Action="deleteSalaryRecord";
    $.post(url,message,function (json) {
            if (json.ok === 200) {
                window.location.href="check_table.html";
                alert("删除成功！");
            } else {
                alert("删除失败！");
            }
        }
    )
}

function submitModifyRecord(){
    let url="../salaryModule_file_servlet_action";
    let message={};
    message.Action="modifySalaryRecord";
    message.salary_id=salaryId;
    message.employee_id=$("#ModifySalaryRecord #employee_id").val();
    message.employee_name=$("#ModifySalaryRecord #employee_name").val();
    message.employee_duty=$("#ModifySalaryRecord #employee_duty").val();
    message.salary_number=$("#ModifySalaryRecord #salary_number").val();
    message.salary_remark=$("#ModifySalaryRecord #salary_remark").val();
    message.salary_month=$("#ModifySalaryRecord #salary_month").val();
    $.post(url,message,function (json) {
            if (json.ok === 200) {
                window.location.href="check_table.html";
                alert("修改成功！");
            } else {
                // 否则登录失败
                alert("修改失败！");
            }
        }
    )
    $("#ModifySalaryRecord").hide();
}

function submitQueryRecord(){
    $('.datatable').dataTable( {
        "destroy":true,
        "paging":true,
        "searching":false,
        "oLanguage": {
            "aria": {
                "sortAscending": ": activate to sort column ascending",
                "sortDescending": ": activate to sort column descending"
            },
            "sProcessing":   "处理中...",
            "sLengthMenu":   "_MENU_ 记录/页",
            "sZeroRecords":  "没有匹配的记录",
            "sInfo":         "显示第 _START_ 至 _END_ 项记录，共 _TOTAL_ 项",
            "sInfoEmpty":    "显示第 0 至 0 项记录，共 0 项",
            "sInfoFiltered": "(由 _MAX_ 项记录过滤)",
            "sInfoPostFix":  "",
            "sSearch":       "过滤:",
            "oPaginate": {
                "sFirst":    "首页",
                "sPrevious": "上页",
                "sNext":     "下页",
                "sLast":     "末页"
            }
        },
        "aoColumns": [{"mRender": function(data, type, full) {
                sReturn = '<input type="checkbox" class="checkboxes" value="'+full.salary_id+'"/>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.employee_id+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.employee_name+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.employee_duty+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.salary_number+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.salary_month+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.salary_remark+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.salary_datetime+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+"<a onclick=\"submitDeleteRecord("+full.salary_id+")\">删除</a>"+'</div>';
                sReturn += '<div>'+"<a onclick=\"onModifyRecord("+full.salary_id+")\">修改</a>"+'</div>';
                return sReturn;
            },"orderable": false
        }],


        "aLengthMenu": [[5,10,15,20,25,40,50,-1],[5,10,15,20,25,40,50,"所有记录"]],
        "fnDrawCallback": function(){$(".checkboxes").uniform();$(".group-checkable").uniform();},
        //"sAjaxSource": "get_record.jsp"
        "sAjaxSource": "../salaryModule_file_servlet_action?Action=querySalaryRecord&employee_name="+$("#QuerySalaryRecord #employee_name").val()+"&salary_month="+$("#QuerySalaryRecord #salary_month").val(),
    });
    $('.datatable').find('.group-checkable').change(function () {
        var set = jQuery(this).attr("data-set");
        var checked = jQuery(this).is(":checked");
        jQuery(set).each(function () {
            if (checked) {
                $(this).attr("checked", true);
                $(this).parents('tr').addClass("active");
            } else {
                $(this).attr("checked", false);
                $(this).parents('tr').removeClass("active");
            }
        });
        jQuery.uniform.update(set);
    });
    $('.datatable').on('change', 'tbody tr .checkboxes', function () {
        $(this).parents('tr').toggleClass("active");
    });
    $("#QuerySalaryRecord").hide();
}

function onDeleteRecord(){
    $(".tip").show();
}

