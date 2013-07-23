#Play 2 Crud module

A flexible Play framework 2.1 crud module. This module is in development, many features are missing and there is no documentation yet.

##Usage

### Dependencies

Add repository (snapshots for now) in `Build.scala` to your resolvers

	resolvers += Resolver.url("njin github repository (snapshots)", url("http://njin-fr.github.com/repository/snapshots"))(Resolver.ivyStylePatterns),

Add the core module dependency

	"fr.njin" % "play-crud" % "1.0.0-SNAPSHOT"

Add your data provider (only ebean for now)

    "fr.njin" % "play-crud-ebean" % "1.0.0-SNAPSHOT"
    
### Initialize

play-crud use play-2.1 controller instances for his routes (see [Managing Controller Class Instantiation](http://www.playframework.org/documentation/2.1-RC4/JavaInjection)). So you will have to use Global settings in your application (see [Global settings at playframework.org](http://www.playframework.org/documentation/2.1-RC4/JavaGlobal)).

First, initialize the manager on your application start:

	ControllersManager.init(new EbeanModelRegistry(application),
                new ClasspathScannerControllerRegistry(application),
                new EbeanDataProviderFactory());
	
Then, provide the instance to play:

	@SuppressWarnings("unchecked")
	public <A> A getControllerInstance(Class<A> clazz) throws Exception {
		if(clazz.equals(ControllersManager.class)) {
            ControllersManager manager = ControllersManager.getInstance();
            return (A)manager;
		}
		return super.getControllerInstance(clazz);
	}

Finaly, add routes of the module in your `routes.conf`:

	->		/admin						play.crud.Routes

See [ebean sample](play-crud-tests/play-crud-ebean/) for an example.

### Add some controllers

Create a Class in your app classpath that extends `fr.njin.play.crud.controllers.Crud`. Exemple with an entity `Task` with a `Long` id:

	public class Tasks extends Crud<Long, Task> {
    	public Tasks() {
        	super(Long.class, Task.class);
	    }
	}
	
play-crud will scan your classpath and add this controller to his list.

## License

### The MIT License

Copyright (c) 2013 njin • http://www.njin.fr

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.