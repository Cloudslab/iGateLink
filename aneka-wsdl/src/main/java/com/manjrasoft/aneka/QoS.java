package com.manjrasoft.aneka;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class QoS extends SoapObject implements Deserializable {

	/** Mandatory property */
	private double budgetSpent;

	/** Mandatory property */
	private java.util.Calendar deadline;

	/** Mandatory property */
	private double budget;

	/** Mandatory property */
	private String optimizationStrategy;

	/** Mandatory property */
	private TimeSpan averageTaskExecutionTime;

	/** Mandatory property */
	private int totalWork;

	/** Mandatory property */
	private int scheduledTasks;

	/** Mandatory property */
	private int workCompleted;

	/** Optional property */
	private com.manjrasoft.aneka.NodeRequirement nodeRequirement;

	public QoS() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "QoS");
	}

	protected QoS(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(QoS object, AttributeContainer response) {
		object.setBudgetSpent(KSoap2Utils.getDouble(response, "BudgetSpent"));
		object.setDeadline(KSoap2Utils.getCalendar(response, "Deadline"));
		object.setBudget(KSoap2Utils.getDouble(response, "Budget"));
		object.setOptimizationStrategy(KSoap2Utils.getString(response, "OptimizationStrategy"));
		Object averageTaskExecutionTimeValue = KSoap2Utils.getProperty((SoapObject) response, "AverageTaskExecutionTime");
		object.setAverageTaskExecutionTime(averageTaskExecutionTimeValue != null ? (TimeSpan) KSoap2Utils.getObject(new TimeSpan(), (AttributeContainer) averageTaskExecutionTimeValue) : null);
		object.setTotalWork(KSoap2Utils.getInteger(response, "TotalWork"));
		object.setScheduledTasks(KSoap2Utils.getInteger(response, "ScheduledTasks"));
		object.setWorkCompleted(KSoap2Utils.getInteger(response, "WorkCompleted"));
		Object nodeRequirementValue = KSoap2Utils.getProperty((SoapObject) response, "NodeRequirement");
		object.setNodeRequirement(nodeRequirementValue != null ? (com.manjrasoft.aneka.NodeRequirement) KSoap2Utils.getObject(new com.manjrasoft.aneka.NodeRequirement(), (AttributeContainer) nodeRequirementValue) : null);
	}

	public int getPropertyCount() {
		return 9;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return budgetSpent;
		case 1:
			return deadline != null ? org.kobjects.isodate.IsoDate.dateToString(deadline.getTime(),
				org.kobjects.isodate.IsoDate.DATE_TIME) : null;
		case 2:
			return budget;
		case 3:
			return optimizationStrategy;
		case 4:
			return averageTaskExecutionTime;
		case 5:
			return totalWork;
		case 6:
			return scheduledTasks;
		case 7:
			return workCompleted;
		case 8:
			return nodeRequirement;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "BudgetSpent";
			info.type = double.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 1:
			info.name = "Deadline";
			info.type = java.lang.String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 2:
			info.name = "Budget";
			info.type = double.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 3:
			info.name = "OptimizationStrategy";
			info.type = java.lang.String.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 4:
			info.name = "AverageTaskExecutionTime";
			info.type = TimeSpan.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 5:
			info.name = "TotalWork";
			info.type = int.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 6:
			info.name = "ScheduledTasks";
			info.type = int.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 7:
			info.name = "WorkCompleted";
			info.type = int.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		case 8:
			info.name = "NodeRequirement";
			info.type = com.manjrasoft.aneka.NodeRequirement.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public double getBudgetSpent() {
		return budgetSpent;
	}

	public void setBudgetSpent(double newValue) {
		budgetSpent = newValue;
	}

	public java.util.Calendar getDeadline() {
		return deadline;
	}

	public void setDeadline(java.util.Calendar newValue) {
		deadline = newValue;
	}

	public double getBudget() {
		return budget;
	}

	public void setBudget(double newValue) {
		budget = newValue;
	}

	public String getOptimizationStrategy() {
		return optimizationStrategy;
	}

	public void setOptimizationStrategy(String newValue) {
		optimizationStrategy = newValue;
	}

	public TimeSpan getAverageTaskExecutionTime() {
		return averageTaskExecutionTime;
	}

	public void setAverageTaskExecutionTime(TimeSpan newValue) {
		averageTaskExecutionTime = newValue;
	}

	public int getTotalWork() {
		return totalWork;
	}

	public void setTotalWork(int newValue) {
		totalWork = newValue;
	}

	public int getScheduledTasks() {
		return scheduledTasks;
	}

	public void setScheduledTasks(int newValue) {
		scheduledTasks = newValue;
	}

	public int getWorkCompleted() {
		return workCompleted;
	}

	public void setWorkCompleted(int newValue) {
		workCompleted = newValue;
	}

	public com.manjrasoft.aneka.NodeRequirement getNodeRequirement() {
		return nodeRequirement;
	}

	public void setNodeRequirement(com.manjrasoft.aneka.NodeRequirement newValue) {
		nodeRequirement = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("QoS [");
		sb.append("budgetSpent=").append(budgetSpent);
		sb.append(", ");
		sb.append("deadline=").append(deadline != null ? deadline.getTime() : null);
		sb.append(", ");
		sb.append("budget=").append(budget);
		sb.append(", ");
		sb.append("optimizationStrategy=").append(optimizationStrategy);
		sb.append(", ");
		sb.append("averageTaskExecutionTime=").append(averageTaskExecutionTime);
		sb.append(", ");
		sb.append("totalWork=").append(totalWork);
		sb.append(", ");
		sb.append("scheduledTasks=").append(scheduledTasks);
		sb.append(", ");
		sb.append("workCompleted=").append(workCompleted);
		sb.append(", ");
		sb.append("nodeRequirement=").append(nodeRequirement);
		return sb.append(']').toString();
	}
}
