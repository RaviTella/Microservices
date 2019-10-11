using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using Microsoft.Extensions.Configuration;
using eBookStore.Models;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using Polly;
using Polly.CircuitBreaker;
using Polly.Fallback;
using Polly.Wrap;

namespace eBookStore.Controllers {
    public class HomeController : Controller {
         private IConfiguration _config;
        private static HttpClient _client;
       // private IHttpClientFactory _clientFactory;
        private AsyncCircuitBreakerPolicy breaker;
        private AsyncPolicyWrap<string> fallback;

        public HomeController (IConfiguration
         config, IHttpClientFactory clientFactory ) {          
            _config = config;
            _client= clientFactory.CreateClient();
            breaker = Policy
                .Handle<Exception> ()
                .CircuitBreakerAsync (
                    exceptionsAllowedBeforeBreaking: 3,
                    durationOfBreak: TimeSpan.FromMinutes (1)
                );

            fallback = Policy<string>
                .Handle<Exception> ()
                .FallbackAsync (cancellationToken => fallbackAction (),
                    ex => {
                        return Task.CompletedTask;
                    })
                .WrapAsync (breaker);

        }

        public async Task<IActionResult> Index () {
            
            var recommendationsTask = fallback.ExecuteAsync (() => _client.GetStringAsync(_config.GetValue<string>("externalRestServices:recommendationService")));
            var viewedItemsTask = _client.GetStringAsync(_config.GetValue<string>("externalRestServices:viewedItemsService"));
            var cartTask = _client.GetStringAsync(_config.GetValue<string>("externalRestServices:cartService"));
            var customerTask = _client.GetStringAsync(_config.GetValue<string>("externalRestServices:customerService"));

            var response = await Task.WhenAll (recommendationsTask, viewedItemsTask, cartTask, customerTask);

            CompositeModel model = new CompositeModel {
                Recommendations = JsonConvert.DeserializeObject<IEnumerable<Item>> (response[0]),
                ViewedItems = JsonConvert.DeserializeObject<IEnumerable<Item>> (response[1]),
                CartItems = JsonConvert.DeserializeObject<IEnumerable<Item>> (response[2]),
                Customers = JsonConvert.DeserializeObject<IEnumerable<Customer>> (response[3])
            };
            return View (model);
        }

        public Task<string> fallbackAction () {
            return Task.Run (() => "[" + JsonConvert.SerializeObject (new Item { id = "-1" }) + "]");;
        }
    }
}