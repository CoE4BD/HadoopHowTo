# MRUnit: with vs. add
Brad Rubin  
9/30/2013

### MRUnit supports two diffent input/output methods, add and with.  Here is the difference.
---
MRUnit has two forms of input and output methods, withInput()/addInput() and withOutput()/addOutput().  At first glance, both apear do do the same thing.  However, the with methods allow a programming style called a *fluent interface*, which in Java means that the method call returns self and the methods can be chained together.  Here, we illustrate a mapper test with both programming styles.  Both function the same, so this is a matter of style.

See this blog post for more information on the [fluent interface](http://martinfowler.com/bliki/FluentInterface.html).

## add

	@Test
	public void testMapper() throws IOException {

		mapDriver.addInput(
						new LongWritable(1),
						new Text( "NYSE,ZTR,1.0"));
		mapDriver.addInput(
						new LongWritable(1),
						new Text("NYSE,ZTR,2.0"));
		mapDriver.addInput(
						new LongWritable(1),
						new Text("NYSE,AAA,3.0"));
		mapDriver.addInput(
						new LongWritable(1),
						new Text("Dirty Data"));
		mapDriver.addOutput(
						new Text("ZTR"),
						new FloatWritable((float)1.0);
		mapDriver.addOutput(
						new Text("ZTR"),
						new FloatWritable((float)2.0);
		mapDriver.addOutput(
						new Text("AAA"),
						new FloatWritable((float)3.0);
		mapDriver.runTest();
	}
	
## with

	@Test
	public void testMapper() throws IOException {

		mapDriver
				.withInput(
						new LongWritable(1),
						new Text( "NYSE,ZTR,1.0"))
				.withInput(
						new LongWritable(1),
						new Text("NYSE,ZTR,2.0"))
				.withInput(
						new LongWritable(1),
						new Text("NYSE,AAA,3.0"))
				.withInput(
						new LongWritable(1),
						new Text("Dirty Data"));
		mapDriver
				.withOutput(
						new Text("ZTR"),
						new FloatWritable((float)1.0)
				.withOutput(
						new Text("ZTR"),
						new FloatWritable((float)2.0)
				.withOutput(
						new Text("AAA"),
						new FloatWritable((float)3.0);
		mapDriver.runTest();
	}
