using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using eBookStore.Models;
using System.Net.Http;
using Newtonsoft.Json;
using Polly.CircuitBreaker;
using Polly.Fallback;
using Polly;
using Polly.Wrap;

namespace eBookStore.Controllers
{
    public class HomeController : Controller
    {
        private static HttpClient Client = new HttpClient();
        private AsyncCircuitBreakerPolicy breaker;
        private AsyncPolicyWrap<string> fallback;

        public HomeController()
        {
            breaker = Policy
     .Handle<Exception>()
     .CircuitBreakerAsync(
         exceptionsAllowedBeforeBreaking: 2,
         durationOfBreak: TimeSpan.FromMinutes(1)
     );

            fallback = Policy<string>
           .Handle<Exception>()
           .FallbackAsync(cancellationToken => fallbackAction(),
        ex =>
        {
            return Task.CompletedTask;
        }
    )
           .WrapAsync(breaker);

        }

        public async Task<IActionResult> Index()
        {
            var recommendationsTask = fallback.ExecuteAsync(() => Task.Run(() => Client.GetStringAsync("http://localhost:9001/recommendations/customer/1001")));
            //var recommendationsTask = Task.Run(() => Client.GetStringAsync("http://localhost:9001/recommendations/customer/1001"));
            var viewedItemsTask = Task.Run(() => Client.GetStringAsync("http://52.224.136.196:9000/viewedItems/customer/1001"));
            var cartTask = Task.Run(() => Client.GetStringAsync("http://52.191.234.203:9000/cart/customer/1001"));
            var customerTask = Task.Run(() => Client.GetStringAsync("http://52.190.26.105:9000/customer/1001"));

            var response = await Task.WhenAll(recommendationsTask, viewedItemsTask, cartTask, customerTask);
            Console.WriteLine(response[0]);
            CompositeModel model = new CompositeModel
            {        
                       
                Recommendations = JsonConvert.DeserializeObject<IEnumerable<Item>>(response[0]),
                ViewedItems = JsonConvert.DeserializeObject<IEnumerable<Item>>(response[1]),
                CartItems = JsonConvert.DeserializeObject<IEnumerable<Item>>(response[2]),
                Customers = JsonConvert.DeserializeObject<IEnumerable<Customer>>(response[3])
            };
            return View(model);
        }

        public Task<string> fallbackAction()
        {
            return Task.Run(() => "["+JsonConvert.SerializeObject(new Item{id="-1"})+"]"); ;
        }

     }
}
